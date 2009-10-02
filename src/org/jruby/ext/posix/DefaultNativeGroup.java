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

package org.jruby.ext.posix;

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
    public final UTF8StringRef gr_name = new UTF8StringRef();   // name
    public final UTF8StringRef gr_passwd = new UTF8StringRef(); // group password (encrypted)
    public final Signed32 gr_gid = new Signed32();       // group id
    public final Pointer gr_mem = new Pointer();
    
    DefaultNativeGroup(com.kenai.jaffl.Pointer memory) {
        useMemory(memory);
    }
    
    public java.lang.String getName() {
        return gr_name.get();
    }
    public java.lang.String getPassword() {
        return gr_passwd.get();
    }
    public long getGID() {
        return gr_gid.get();
    }
    public java.lang.String[] getMembers() {
        int size = com.kenai.jaffl.Pointer.SIZE / 8;
        int i=0;
        List<java.lang.String> lst = new ArrayList<java.lang.String>();
        com.kenai.jaffl.Pointer ptr = gr_mem.get();
        while (ptr.getPointer(i) != null) {
            lst.add(ptr.getPointer(i).getString(0));
            i+=size;
        }
        return lst.toArray(new java.lang.String[0]);
    }

}
