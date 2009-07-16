/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath"
 * exception as provided by Sun in the License file that accompanied
 * this code.
 */
package org.jdesktop.wonderland.client.jme.artimport;

import com.jme.math.Vector3f;
import java.io.Serializable;
import java.net.URL;

/**
 *
 * @author paulby
 */
public class Model implements Serializable {

    private URL originalFile; // Original file from which model was loaded

    private Vector3f translation;
    private Vector3f orientation;
    private String wonderlandName;

    public Model(URL originalFile) {
        this.originalFile = originalFile;
    }

    /**
     * Get the urls of all the model files, [0] is the primary file
     * @return
     */
    public URL[] getAllOriginalModels() {
        return new URL[] {originalFile};
    }

    /**
     * @return the originalFile
     */
    public URL getOriginalURL() {
        return originalFile;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    public Vector3f getOrientation() {
        return orientation;
    }

    public void setOrientation(Vector3f orientation) {
        this.orientation = orientation;
    }

    public String getWonderlandName() {
        return wonderlandName;
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer();

        ret.append("Model : "+originalFile.toExternalForm());

        return ret.toString();
    }
}