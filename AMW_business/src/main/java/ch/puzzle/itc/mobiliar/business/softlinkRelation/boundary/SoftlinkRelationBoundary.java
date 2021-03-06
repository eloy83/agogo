/*
 * AMW - Automated Middleware allows you to manage the configurations of
 * your Java EE applications on an unlimited number of different environments
 * with various versions, including the automated deployment of those apps.
 * Copyright (C) 2013-2016 by Puzzle ITC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.puzzle.itc.mobiliar.business.softlinkRelation.boundary;

import java.io.Serializable;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;

import ch.puzzle.itc.mobiliar.business.foreignable.control.ForeignableService;
import ch.puzzle.itc.mobiliar.business.foreignable.entity.ForeignableOwner;
import ch.puzzle.itc.mobiliar.business.foreignable.entity.ForeignableOwnerViolationException;
import ch.puzzle.itc.mobiliar.business.releasing.entity.ReleaseEntity;
import ch.puzzle.itc.mobiliar.business.resourcegroup.entity.ResourceEntity;
import ch.puzzle.itc.mobiliar.business.security.entity.Permission;
import ch.puzzle.itc.mobiliar.business.security.interceptor.HasPermission;
import ch.puzzle.itc.mobiliar.business.security.interceptor.HasPermissionInterceptor;
import ch.puzzle.itc.mobiliar.business.softlinkRelation.control.SoftlinkRelationService;
import ch.puzzle.itc.mobiliar.business.softlinkRelation.entity.SoftlinkRelationEntity;

@Stateless
@Interceptors(HasPermissionInterceptor.class)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class SoftlinkRelationBoundary implements Serializable {

    @Inject
    private SoftlinkRelationService softlinkRelationService;

    @Inject
    private ForeignableService foreignableService;

    @Inject
    private EntityManager entityManager;

    @HasPermission(permission = Permission.SET_SOFTLINK_ID_OR_REF)
    public void createSoftlinkRelation(ForeignableOwner creatingOwner, Integer cpiResourceId, String softlinkReference) throws ForeignableOwnerViolationException {
        ResourceEntity cpiResource = entityManager.find(ResourceEntity.class, cpiResourceId);

        if (cpiResource != null) {

            SoftlinkRelationEntity softlinkRelation = cpiResource.getSoftlinkRelation();

            if (softlinkRelation == null){
                softlinkRelation = new SoftlinkRelationEntity();
                softlinkRelation.setOwner(creatingOwner);
                softlinkRelation.setCpiResource(cpiResource);
                softlinkRelation.setSoftlinkRef(softlinkReference);

            } else {
                int beforeChangeForeignableFieldHashCode = softlinkRelation.foreignableFieldHashCode();
                softlinkRelation.setSoftlinkRef(softlinkReference);
                foreignableService.verifyEditableByOwner(creatingOwner, beforeChangeForeignableFieldHashCode, softlinkRelation);
            }

            softlinkRelationService.setSoftlinkRelation(cpiResource, softlinkRelation);

        } else {
            throw new RuntimeException("No resource found for id "+ cpiResourceId);
        }
    }

    @HasPermission(permission = Permission.SET_SOFTLINK_ID_OR_REF)
    public void removeRelationForResource(ForeignableOwner deletingOwner, Integer resourceId) throws ForeignableOwnerViolationException {
        ResourceEntity cpiResource = entityManager.find(ResourceEntity.class, resourceId);
        if (cpiResource != null) {
            SoftlinkRelationEntity softlinkRelation = cpiResource.getSoftlinkRelation();
            if (softlinkRelation != null){
                foreignableService.verifyDeletableByOwner(deletingOwner, softlinkRelation);

                softlinkRelationService.removeSoftlinkRelation(cpiResource);
            }

        } else {
            throw new RuntimeException("No resource found for id "+ resourceId);
        }
    }

    @HasPermission(permission = Permission.SET_SOFTLINK_ID_OR_REF)
    public void editSoftlinkRelation(ForeignableOwner editingUser, SoftlinkRelationEntity editedSoftlinkRelation) throws ForeignableOwnerViolationException {
        SoftlinkRelationEntity softlinkRelation = entityManager.find(SoftlinkRelationEntity.class, Objects.requireNonNull(editedSoftlinkRelation, "editedSoftlinkRelation must not be null!").getId());

        if (softlinkRelation != null) {
            int beforeChangeForeignableFieldHashCode = softlinkRelation.foreignableFieldHashCode();
            SoftlinkRelationEntity mergedSoftlinkRelation = entityManager.merge(editedSoftlinkRelation);
            foreignableService.verifyEditableByOwner(editingUser, beforeChangeForeignableFieldHashCode, mergedSoftlinkRelation);

        } else {
            throw new RuntimeException("No softlink relation found for "+ editedSoftlinkRelation);
        }
    }
    
    public ResourceEntity getSoftlinkResolvableSlaveResource(SoftlinkRelationEntity softlinkRelation, ReleaseEntity release){
        if (softlinkRelation != null){
            return softlinkRelationService.getSoftlinkResolvableSlaveResource(softlinkRelation.getSoftlinkRef(), release);
        }
        return null;
    }

}
