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

package jnr.posix;

import jnr.ffi.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The default native group layout.
 * 
 * <p>
 * This implementation should work on Solaris, Linux and MacOS.
 * </p>
 */
public final class DefaultNativeGroup extends NativeGroup implements Group {
    static final class Layout extends StructLayout {
        public Layout(jnr.ffi.Runtime runtime) {
            super(runtime);
        }

        public final UTF8StringRef gr_name = new UTF8StringRef();   // name
        public final UTF8StringRef gr_passwd = new UTF8StringRef(); // group password (encrypted)
        public final Signed32 gr_gid = new Signed32();       // group id
        public final Pointer gr_mem = new Pointer();
    }

    static final Layout layout = new Layout(jnr.ffi.Runtime.getSystemRuntime());
    private final Pointer memory;

    DefaultNativeGroup(jnr.ffi.Pointer memory) {
        super(memory.getRuntime(), layout);
        this.memory = memory;
    }

    public java.lang.String getName() {
        return layout.gr_name.get(memory);
    }

    public java.lang.String getPassword() {
        return layout.gr_passwd.get(memory);
    }

    public long getGID() {
        return layout.gr_gid.get(memory);
    }

    public java.lang.String[] getMembers() {
        List<java.lang.String> lst = new ArrayList<java.lang.String>();

        jnr.ffi.Pointer ptr = layout.gr_mem.get(memory);
        Pointer member;
        int ptrSize = runtime.addressSize();
        for (int i = 0; (member = ptr.getPointer(i)) != null; i += ptrSize) {
            lst.add(member.getString(0));
        }

        return lst.toArray(new java.lang.String[lst.size()]);
    }

}
