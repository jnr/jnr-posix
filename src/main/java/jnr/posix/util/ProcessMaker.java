/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
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

/**
 * Abstract interface for building a process on various JVM versions.
 */
public interface ProcessMaker {
    public static class Redirect {
        public static final Redirect INHERIT = new Redirect(Type.INHERIT);
        public static final Redirect PIPE = new Redirect(Type.PIPE);

        private enum Type {
            APPEND,
            INHERIT,
            PIPE,
            READ,
            WRITE
        }

        private final Type type;
        private final File file;

        private Redirect(Type type) {
            this(type, null);
        }

        private Redirect(Type type, File file) {
            this.type = type;
            this.file = file;
        }

        public static Redirect appendTo(File file) {
            return new Redirect(Type.APPEND, file);
        }

        public static Redirect from(File file) {
            return new Redirect(Type.READ, file);
        }

        public static Redirect to(File file) {
            return new Redirect(Type.WRITE, file);
        }

        public File file() {
            return file;
        }

        public Type type() {
            return type;
        }
    }

    public List<String> command();
    public ProcessMaker command(List<String> command);
    public ProcessMaker command(String... command);
    public File directory();
    public ProcessMaker directory(File dir);
    public Map<String, String> environment();
    public ProcessMaker environment(String[] envLines);
    public ProcessMaker inheritIO();
    public Redirect redirectError();
    public ProcessMaker redirectError(File file);
    public ProcessMaker redirectError(Redirect destination);
    public boolean redirectErrorStream();
    public ProcessMaker redirectErrorStream(boolean redirectErrorStream);
    public Redirect redirectInput();
    public ProcessMaker redirectInput(File file);
    public ProcessMaker redirectInput(Redirect source);
    public Redirect redirectOutput();
    public ProcessMaker redirectOutput(File file);
    public ProcessMaker redirectOutput(Redirect destination);
    public Process start() throws IOException;
}
