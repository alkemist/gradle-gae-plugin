/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.plugins.gae.task.appcfg

import org.gradle.api.InvalidUserDataException

/**
 * Google App Engine task downloading application logs from the server.
 *
 * @see <a href="http://code.google.com/appengine/docs/java/tools/uploadinganapp.html#Downloading_Logs">Documentation</a>
 * @author Benjamin Muschko
 */
class GaeDownloadLogsTask extends GaeAppConfigTaskTemplate {
    final String COMMAND = "request_logs"
    private Integer numDays
    private Integer severity
    private Boolean append
    private File outputFile

    @Override
    void validateConfiguration(){
        super.validateConfiguration()

        if(getSeverity() && (getSeverity() < 0 || getSeverity() > 4)) {
            throw new InvalidUserDataException("Invalid log level: ${getSeverity()}. Valid values are 4 for CRITICAL, 3 for ERROR, 2 for WARNING, 1 for INFO, 0 for DEBUG.")
        }
        else {
            logger.info "Pulling messages with minimum log level = ${getSeverity()}"
        }
    }

    @Override
    String startLogMessage() {
        "Starting downloading logs process..."
    }

    @Override
    String errorLogMessage() {
        "An error occurred downloading logs from App Engine."
    }

    @Override
    String finishLogMessage() {
        "Finished downloading logs process."
    }

    @Override
    List getParams() {
        def params = []

        if(getNumDays()) {
            params << "--num_days=${getNumDays()}"
        }

        if(getSeverity()) {
            params << "--severity=${getSeverity()}"
        }

        if(getAppend()) {
            params << "--append"
        }

        params << COMMAND
        params << getWebAppSourceDirectory().getCanonicalPath()
        params << getOutputFile().getCanonicalFile()
        params
    }

    public Integer getNumDays() {
        numDays
    }

    public void setNumDays(Integer numDays) {
        this.numDays = numDays
    }

    public Integer getSeverity() {
        severity
    }

    public void setSeverity(Integer severity) {
        this.severity = severity
    }

    public Boolean getAppend() {
        append
    }

    public void setAppend(Boolean append) {
        this.append = append
    }

    public File getOutputFile() {
        outputFile
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile
    }
}
