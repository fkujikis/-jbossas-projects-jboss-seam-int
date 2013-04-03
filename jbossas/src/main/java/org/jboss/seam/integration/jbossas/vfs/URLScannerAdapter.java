package org.jboss.seam.integration.jbossas.vfs;

import java.util.Collections;

import org.jboss.seam.deployment.DeploymentStrategy;
import org.jboss.seam.deployment.URLScanner;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class URLScannerAdapter extends URLScanner {
    public URLScannerAdapter(DeploymentStrategy deploymentStrategy) {
        super(deploymentStrategy);
    }

    void handlePath(String path) {
        handle(Collections.singleton(path));
    }
}
