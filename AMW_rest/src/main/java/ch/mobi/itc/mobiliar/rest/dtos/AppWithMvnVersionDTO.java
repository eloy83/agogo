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

package ch.mobi.itc.mobiliar.rest.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

/**
* @deprecated Only here for backwards compatibility of the rest API
*/
@XmlRootElement(name = "appWithMvnVersion")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter @Setter
@Deprecated
public class AppWithMvnVersionDTO {

	private String applicationName;
	private String mavenVersion;

	public AppWithMvnVersionDTO() {
	}

	public AppWithMvnVersionDTO(String applicationName, String mavenVersion) {
		this.applicationName = applicationName;
		this.mavenVersion = mavenVersion;
	}

}