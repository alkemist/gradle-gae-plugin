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
package org.gradle.api.plugins.gae.task.internal

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Monitor that keeps thread running until stop command got issued.
 *
 * @author Benjamin Muschko
 */
class ShutdownMonitor extends Thread {
    static final Logger logger = LoggerFactory.getLogger(ShutdownMonitor.class)
    final int port
    final String key
    final ShutdownCallback shutdownCallback
    ServerSocket serverSocket

    public ShutdownMonitor(int port, String key, ShutdownCallback shutdownCallback) {
        if(port <= 0) {
            throw new IllegalStateException("Bad stop port")
        }

        this.port = port
        this.key = key
        this.shutdownCallback = shutdownCallback

        setName("GaePluginShutdownMonitor");
        serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"))
    }

    @Override
    void run() {
        while(serverSocket != null) {
            Socket socket = null;

            try {
                socket = serverSocket.accept()
                socket.setSoLinger(false, 0)
                LineNumberReader lin = new LineNumberReader(new InputStreamReader(socket.getInputStream()))

                String keyCmd = lin.readLine()

                if(key && !(key == keyCmd)) {
                    continue
                }

                String cmd = lin.readLine()

                if("stop".equals(cmd)) {
                    logger.info "Shutting down server"

                    try {
                        socket.close()
                    }
                    catch(Exception e) {
                        logger.debug "Exception when stopping server", e
                    }
                    try {
                        serverSocket.close()
                    }
                    catch(IOException e) {
                        logger.debug "Exception when stopping server", e
                    }

                    serverSocket = null
                    shutdownCallback.onShutdown()
                }
            }
            catch(Exception e) {
                logger.error "Exception in monitoring monitor", e
                System.exit(1)
            }
            finally {
                if(socket != null) {
                    try {
                        socket.close()
                    }
                    catch(Exception e) {
                        logger.debug "Exception when stopping server", e
                    }
                }

                socket = null
            }
        }
    }
}
