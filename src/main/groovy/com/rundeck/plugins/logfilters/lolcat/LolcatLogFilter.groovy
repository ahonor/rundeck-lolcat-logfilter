/*
 * Copyright 2017 Rundeck, Inc. (http://rundeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rundeck.plugins.log

import com.dtolabs.rundeck.core.execution.workflow.OutputContext
import com.dtolabs.rundeck.core.logging.LogEventControl
import com.dtolabs.rundeck.core.logging.LogLevel
import com.dtolabs.rundeck.core.logging.PluginLoggingContext
import com.dtolabs.rundeck.core.plugins.Plugin
import com.dtolabs.rundeck.plugins.ServiceNameConstants
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription
import com.dtolabs.rundeck.plugins.descriptions.PluginMetadata
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty
import com.dtolabs.rundeck.plugins.logging.LogFilterPlugin
import com.github.jamesnetherton.lolcat4j.Lol
import com.github.jamesnetherton.lolcat4j.internal.console.ConsolePainter
import com.github.jamesnetherton.lolcat4j.internal.console.ConsolePrinter

// See this example code for reference:
//   https://github.com/jamesnetherton/lolcat4j/blob/master/src/test/java/com/github/jamesnetherton/lolcat4j/internal/console/ConsolePainterTest.java

@Plugin(name = LolcatLogFilterPlugin.PROVIDER_NAME, service = ServiceNameConstants.LogFilter)
@PluginDescription(title = 'Lolcat',
        description = 'Adds rainbow coloring to your text.')
@PluginMetadata(key = 'faicon', value = 'fa-cat')
class LolcatLogFilterPlugin implements LogFilterPlugin {
    public static final String PROVIDER_NAME = 'lolcat'

    @PluginProperty(
            title = 'frequency',
            description = '''The rainbow frequency (default 0.1).''',
            defaultValue = '0.1'
    )
    String frequency

    @PluginProperty(
            title = 'spread',
            description = '''The rainbow spread (default 3.0).''',
            defaultValue = '3.0'
    )
    String spread

    private StringBuffer buffer
    OutputContext outputContext

    @Override
    void init(final PluginLoggingContext context) {
        outputContext = context.getOutputContext()
        buffer = new StringBuffer()
    }

    @Override
    void handleEvent(final PluginLoggingContext context, final LogEventControl event) {
        if (event.eventType == 'log' && event.loglevel == LogLevel.NORMAL && event.message?.length() > 0) {
            event.message = toLol(event.message)
            //event.message = "debug: " + event.message
        }
    }

    String toLol(final String text) {
        Lol lol = Lol.builder()
                .seed(1)
                .frequency(0.1)
                .spread(3.0)
                .text(text)
                .build()

        LoggingPrintStream printStream = new LoggingPrintStream(new NonWritableOutputStream())

        //ByteArrayOutputStream baos = new ByteArrayOutputStream()
        //LoggingPrintStream printStream = new LoggingPrintStream(baos);
        //ConsolePainter painter = new ConsolePainter(new ConsolePrinter(baos))

        ConsolePainter painter = new ConsolePainter(new ConsolePrinter(printStream))

        painter.paint(lol)

        //return "TEST: " + text
        return printStream.getLoggedOutput()
    }

}

class LoggingPrintStream extends PrintStream {
    private StringBuilder loggedOutput = new StringBuilder()

    LoggingPrintStream(OutputStream out) {
        super(out)
    }

    @Override
    void println() {
        loggedOutput.append("\n")
    }

    @Override
    void print(String s) {
        loggedOutput.append(s)
    }

    String getLoggedOutput() {
        return loggedOutput.toString()
    }
}

class NonWritableOutputStream extends OutputStream {
    @Override
    void write(int b) throws IOException {
        // Do nothing
    }
}