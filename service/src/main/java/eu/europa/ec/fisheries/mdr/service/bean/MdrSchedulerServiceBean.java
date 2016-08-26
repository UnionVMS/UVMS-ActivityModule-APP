/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
*/
package eu.europa.ec.fisheries.mdr.service.bean;

import eu.europa.ec.fisheries.mdr.domain.ActivityConfiguration;
import eu.europa.ec.fisheries.mdr.repository.MdrRepository;
import eu.europa.ec.fisheries.mdr.service.MdrSchedulerService;
import eu.europa.ec.fisheries.mdr.service.MdrSynchronizationService;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.ejb.*;
import java.util.Collection;

/**
 * @author kovian
 *
 * EJB that provides the MDR Synchronization Functionality.
 * 	1. Methods for handeling the automatic job scheduler configuration.
 */
@Slf4j
@Stateless
public class MdrSchedulerServiceBean implements MdrSchedulerService {

    public static final String MDR_SYNCHRONIZATION_TIMER_NАМЕ = "MDRSynchronizationTimer";
    private static final TimerConfig TIMER_CONFIG             = new TimerConfig(MDR_SYNCHRONIZATION_TIMER_NАМЕ, false);

    @EJB
    private MdrRepository mdrRepository;

    @EJB
    private MdrSynchronizationService synchBean;

    @Resource
    private TimerService timerServ;


    /**
     * Method that will be called when a timer has been set for this EJB.
     *
     */
    @Timeout
    public void timeOut(){
        log.info("\n\t---> STARTING SCHEDULED SYNCHRONIZATION OF MDR ENTITIES! \n");
        synchBean.extractAcronymsAndUpdateMdr();
    }


    /**
     * Gets the actual MDR Synchronization Configuration;
     *
     * @return mdrSynch;
     */
    @Override
    public String getActualSchedulerConfiguration(){
        ActivityConfiguration mdrSynch = mdrRepository.getMdrSchedulerConfiguration();
        return mdrSynch.getConfigValue();
    }

    /**
     * Reconfigures the scheduler and saves the new configuration to MDR Config Table.
     *
     * @param schedulerExpressionStr
     */
    @Override
    public void reconfigureScheduler(String schedulerExpressionStr) {
        log.info("[START] Re-configure MDR scheduler with expression: {}", schedulerExpressionStr);
        if (StringUtils.isNotBlank(schedulerExpressionStr)) {
            // Sometimes unneeded double quotes are present in the posted json string
            schedulerExpressionStr = schedulerExpressionStr.replace("\"", "");
            // Set up the new timer for this EJB;
            setUpScheduler(schedulerExpressionStr);
            // Persist the new config into DB;
            try {
                mdrRepository.changeMdrSchedulerConfiguration(schedulerExpressionStr);
            } catch (ServiceException e) {
                log.error("Error while trying to save the new configuration", e);
            }
            log.info("New MDR scheduler timer created - [{}] - and stored.", TIMER_CONFIG.getInfo());
        } else {
            log.info("[FAILED] Re-configure MDR scheduler with expression: {}. The Scheduler expression is blank.", schedulerExpressionStr);
        }
    }

    @Override
    public void setUpScheduler(String schedulerExpressionStr) {
        Timer newTimer = null;
        try{
            // Parse the Cron-Job expression;
            ScheduleExpression expression = parseExpression(schedulerExpressionStr);
            // Firstly, we need to cancel the current timer, if already exists one;
            cancelPreviousTimer();
            // Set up the new timer for this EJB;
            timerServ.createCalendarTimer(expression, TIMER_CONFIG);;
        } catch(Exception ex){
            log.error("Error creating new scheduled synchronization timer!");
        }
        log.info("New timer scheduler created successfully : ", schedulerExpressionStr);
    }

    /**
     * Cancels the previous set up of the timer.
     *
     */
    private void cancelPreviousTimer() {
        Collection<Timer> allTimers = timerServ.getTimers();
        for (Timer currentTimer: allTimers) {
            if (TIMER_CONFIG.getInfo().equals(currentTimer.getInfo())) {
                currentTimer.cancel();
                log.info("Current MDR scheduler timer cancelled.");
                break;
            }
        }
    }

    /**
     * Creates a ScheduleExpression object with the given schedulerExpressionStr String expression.
     *
     * @param schedulerExpressionStr
     * @return
     */
    private ScheduleExpression parseExpression(String schedulerExpressionStr) {
        ScheduleExpression expression = new ScheduleExpression();
        String[] args = schedulerExpressionStr.split("\\s");
        if (args.length != 6) {
            throw new IllegalArgumentException("Invalid scheduler expression: " + schedulerExpressionStr);
        }
        return expression.second(args[0]).minute(args[1]).hour(args[2]).dayOfMonth(args[3]).month(args[4]).year(args[5]);
    }
}
