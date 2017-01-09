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

package ch.mobi.itc.mobiliar.rest.resources;

import ch.mobi.itc.mobiliar.rest.dtos.ResourceRelationDTO;
import ch.puzzle.itc.mobiliar.business.property.boundary.PropertyEditor;
import ch.puzzle.itc.mobiliar.business.resourcegroup.boundary.ResourceLocator;
import ch.puzzle.itc.mobiliar.business.resourcegroup.entity.ResourceEntity;
import ch.puzzle.itc.mobiliar.business.resourcerelation.boundary.ResourceRelationLocator;
import ch.puzzle.itc.mobiliar.business.resourcerelation.entity.ConsumedResourceRelationEntity;
import ch.puzzle.itc.mobiliar.business.utils.ValidationException;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
@Path("/resources/{resourceGroupName}/{releaseName}/relations")
@Api(value = "/resources/{resourceGroupName}/{releaseName}/relations", description = "Resource relations")
public class ResourceRelationsRest {

    @PathParam("resourceGroupName")
    String resourceGroupName;

    @PathParam("releaseName")
    String releaseName;

    @Inject
    PropertyEditor propertyEditor;

    @Inject
    ResourceLocator resourceLocator;

    @Inject
    ResourceRelationLocator resourceRelationLocator;

    @GET
    @ApiOperation(value = "Get all relations of the current resource")
    public List<ResourceRelationDTO> getResourceRelations() throws ValidationException {
        return getResourceRelations(resourceGroupName, releaseName, false);
    }

    List<ResourceRelationDTO> getResourceRelations(String resourceGroupName, String releaseName, boolean applicationsOnly) throws ValidationException {
        ResourceEntity resource = resourceLocator.getResourceByNameAndReleaseWithRelations(resourceGroupName, releaseName);
        List<ResourceRelationDTO> resourceRelations = new ArrayList<>();
        for (ConsumedResourceRelationEntity relation : resource.getConsumedMasterRelations()) {
            if (applicationsOnly && !relation.getResourceRelationType().getResourceTypeB().isApplicationResourceType()) {
                    continue;
            }
            resourceRelations.add(new ResourceRelationDTO(relation));
        }
        return resourceRelations;
    }


    @Path("/{relatedResourceGroupName}")
    @GET
    @ApiOperation(value = "Get all related releases of the given resource")
    public List<ResourceRelationDTO> getRelatedResourcesForGroup(
            @PathParam("relatedResourceGroupName") String relatedResourceGroupName) throws ValidationException {
        List<ConsumedResourceRelationEntity> relations =
                resourceRelationLocator.getRelatedResourcesForGroup(resourceGroupName, releaseName, relatedResourceGroupName);
        List<ResourceRelationDTO> resourceRelations = new ArrayList<>();
        for (ConsumedResourceRelationEntity relation : relations) {
            resourceRelations.add(new ResourceRelationDTO(relation));
        }
        return resourceRelations;
    }

    @Path("/{relatedResourceGroupName}/{relatedReleaseName}")
    @GET
    @ApiOperation(value = "Get the relation between the two resource releases")
    public ResourceRelationDTO getResourceRelation(@PathParam("relatedResourceGroupName") String relatedResourceGroupName,
                                                   @PathParam("relatedReleaseName") String relatedReleaseName) throws ValidationException {
        return new ResourceRelationDTO(resourceRelationLocator.getResourceRelation(resourceGroupName, releaseName,
                relatedResourceGroupName, relatedReleaseName));
    }

    // Für JavaBatch Monitor: gibt Liste zurück
    @Path("/{relatedResourceGroupName}/{relatedReleaseName}")
    @GET
    @ApiOperation(value = "Get the list of relation between the two resource releases")
    public List<ResourceRelationDTO> getResourceRelationList(@PathParam("relatedResourceGroupName") String relatedResourceGroupName,
                                                             @PathParam("relatedReleaseName") String relatedReleaseName) throws ValidationException {
        List<ResourceRelationDTO> list = new ArrayList<ResourceRelationDTO>();
        for (ConsumedResourceRelationEntity dto : resourceRelationLocator.getResourceRelationList(resourceGroupName, releaseName,
                relatedResourceGroupName, relatedReleaseName)) {
            list.add(new ResourceRelationDTO(dto));
        }
        return list;
    }


}
