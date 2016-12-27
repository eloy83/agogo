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

package ch.puzzle.itc.mobiliar.presentation.resourceDependencies;

import java.io.Serializable;

import lombok.Setter;
import ch.puzzle.itc.mobiliar.business.resourcerelation.entity.AbstractResourceRelationEntity;
import ch.puzzle.itc.mobiliar.business.resourcegroup.entity.ResourceEntity;

/**
 * UI model for displaying resource dependencies
 * 
 * @author cweber
 */
public class DependencyModel implements Serializable {

	private static final long serialVersionUID = 1L;
	

	private AbstractResourceRelationEntity relation;

	public DependencyModel(AbstractResourceRelationEntity relation) {
		this.relation = relation;
	}

	public String getResourceTypeName() {
		return relation.getMasterResource().getResourceType().getName();
	}

	public String getResourceName() {
		return relation.getMasterResource().getName();
	}

	public String getReleaseName() {
		return relation.getMasterResource().getRelease().getName();
	}

	public Integer getResourceId() {
		return relation.getMasterResource().getId();
	}

}
