/*
 * SoapUI, Copyright (C) 2004-2018 SmartBear Software
 *
 * Licensed under the EUPL, Version 1.1 or - as soon as they will be approved by the European Commission - subsequent 
 * versions of the EUPL (the "Licence"); 
 * You may not use this work except in compliance with the Licence. 
 * You may obtain a copy of the Licence at: 
 * 
 * http://ec.europa.eu/idabc/eupl 
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is 
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either 
 * express or implied. See the Licence for the specific language governing permissions and limitations 
 * under the Licence. 
 */

package com.eviware.soapui.support;

import com.eviware.soapui.support.swing.SwingWorkerDelegator;
import com.eviware.x.dialogs.Worker;
import com.eviware.x.dialogs.XProgressDialog;
import com.eviware.x.dialogs.XProgressMonitor;

public class NullProgressDialog implements XProgressDialog {
    public void run(Worker worker) throws Exception {
        SwingWorkerDelegator swingWorker = new SwingWorkerDelegator(new NullProgressMonitor(), this, worker);
        swingWorker.start();
        swingWorker.get();
    }

    public void setVisible(boolean visible) {
    }

    public void setCancelLabel(String label) {
    }

    @Override
    public void setDeterminate() {
    }

    private final static class NullProgressMonitor implements XProgressMonitor {
        public void setProgress(int value, String string) {
            System.out.println("Progress: " + value + " - " + string);
        }
    }
}
