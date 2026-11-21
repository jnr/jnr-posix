/***** BEGIN LICENSE BLOCK *****
 * Version: EPL 2.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Eclipse Public
 * License Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package jnr.posix.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JavaLangProcessMaker implements ProcessMaker {
    protected final ProcessBuilder builder;

    public JavaLangProcessMaker(String... command) {
        builder = new ProcessBuilder(command);
    }

    public JavaLangProcessMaker() {
        builder = new ProcessBuilder();
    }

    public List<String> command() {
        return builder.command();
    }

    public ProcessMaker command(List<String> command) {
        builder.command(command);
        return this;
    }

    public ProcessMaker command(String... command) {
        builder.command(command);
        return this;
    }

    public File directory() {
        return builder.directory();
    }

    public ProcessMaker directory(File dir) {
        builder.directory(dir);
        return this;
    }

    public Map<String, String> environment() {
        return builder.environment();
    }

    public ProcessMaker environment(String[] envLines) {
        envIntoProcessBuilder(builder, envLines);
        return this;
    }

    public ProcessMaker inheritIO() {
        builder.inheritIO();
        return this;
    }

    public Redirect redirectError() {
        return Redirect.fromProcessBuilderRedirect(builder.redirectError());
    }

    public ProcessMaker redirectError(File file) {
        builder.redirectError(file);
        return this;
    }

    public ProcessMaker redirectError(Redirect destination) {
        builder.redirectError(destination.toProcessBuilderRedirect());
        return this;
    }

    public boolean redirectErrorStream() {
        return builder.redirectErrorStream();
    }

    public ProcessMaker redirectErrorStream(boolean redirectErrorStream) {
        builder.redirectErrorStream(redirectErrorStream);
        return this;
    }

    public Redirect redirectInput() {
        return Redirect.fromProcessBuilderRedirect(builder.redirectInput());
    }

    public ProcessMaker redirectInput(File file) {
        builder.redirectInput(file);
        return this;
    }

    public ProcessMaker redirectInput(Redirect source) {
        builder.redirectInput(source.toProcessBuilderRedirect());
        return this;
    }

    public Redirect redirectOutput() {
        return Redirect.fromProcessBuilderRedirect(builder.redirectOutput());
    }

    public ProcessMaker redirectOutput(File file) {
        builder.redirectOutput(file);
        return this;
    }

    public ProcessMaker redirectOutput(Redirect destination) {
        builder.redirectOutput(destination.toProcessBuilderRedirect());
        return this;
    }

    public Process start() throws IOException {
        return builder.start();
    }

    protected static void envIntoProcessBuilder(ProcessBuilder pb, String[] env) {
        if (env == null) return;

        pb.environment().clear();
        for (String envLine : env) {
            if (envLine.indexOf(0) != -1) {
                envLine = envLine.replaceFirst("\u0000.*", "");
            }

            int index = envLine.indexOf('=');

            if (index != -1) {
                pb.environment().put(
                        envLine.substring(0, index),
                        envLine.substring(index + 1));
            }
        }
    }
}
