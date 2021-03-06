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

package ch.puzzle.itc.mobiliar.business.deploy.control;

import ch.puzzle.itc.mobiliar.business.deploy.boundary.DeploymentService;
import ch.puzzle.itc.mobiliar.business.generator.control.GenerationResult;
import ch.puzzle.itc.mobiliar.business.generator.control.GeneratorDomainServiceWithAppServerRelations;
import ch.puzzle.itc.mobiliar.business.generator.control.LockingService;
import ch.puzzle.itc.mobiliar.business.generator.control.extracted.GenerationModus;
import ch.puzzle.itc.mobiliar.business.deploy.entity.DeploymentEntity;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service to start Deployment asynchronously for the generated Configuration
 */
@Stateless
public class DeploymentExecuterService {

	@Inject
	private DeploymentService deploymentService;

	@Inject
	private GeneratorDomainServiceWithAppServerRelations generatorDomainServiceWithAppServerRelations;

	@Inject
	private LockingService lockingService;

	@Inject
	private EntityManager entityManager;

	@Inject
	private DeploymentAsynchronousExecuter deploymentAsynchronousExecuter;

	@Inject 
	private DeploymentExecutionResultHandlerService deploymentExecutionResultHandler;
	
	@Inject
	private Logger log;

	/**
	 * Asynchronous method
	 * 
	 * Reads and generates configuration for the given deploymentId and delegates the effective execution to
	 * the deploymentExecuterService
	 * 
	 * @param deploymentId
	 *             the deploymentId for the given deployment
	 * @param generationModus
	 *             DEPLOY, PREDEPLOY, SIMULATE
	 */
	@Asynchronous
	public void generateConfigurationAndExecuteDeployment(Integer deploymentId, GenerationModus generationModus) {
		DeploymentEntity deployment = deploymentService.getDeploymentById(deploymentId);

		if (deployment != null) {
			// lock Deployment
			boolean lockSuccessful = lockingService.lockDeploymentForExecution(deployment.getId(), generationModus);
			if (lockSuccessful) {
				// Reload the entity because it has been changed by another
				// transaction within the locking mechanism.
				entityManager.clear();
				deployment = entityManager.find(DeploymentEntity.class, deployment.getId());

				// generate Config
				GenerationResult result = null;
				
				try{
					log.info("Generating deployment " + deployment.getId());
					result = generatorDomainServiceWithAppServerRelations.generateConfigurationForDeployment(deployment, generationModus);
				// catch all Exceptions during Generation
				}catch (Exception e) {
					log.log(Level.SEVERE, "Deployment not successful: " + deploymentId, e);
					deploymentExecutionResultHandler.handleUnSuccessfulDeployment(generationModus, deployment, null, e);
				}

				if (result != null && !result.hasErrors()) {
					deploymentAsynchronousExecuter.executeDeployment(result, deployment, generationModus);
				}
			}
		}
		else {
			log.log(Level.SEVERE, "No deployment found for deploymentId " + deploymentId);
		}
	}
}
