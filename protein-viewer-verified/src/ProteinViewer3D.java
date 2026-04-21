import javax.swing.*;
import java.awt.*;

import org.jmol.api.JmolViewer;

public class ProteinViewer3D extends JPanel {

    private JmolViewer viewer;

    public ProteinViewer3D() {

        setLayout(new BorderLayout());

        // 🔥 CREATE PANEL FOR JMOL
        JPanel renderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (viewer != null) {
                    viewer.renderScreenImage(g, getWidth(), getHeight());
                }
            }
        };

        add(renderPanel, BorderLayout.CENTER);

        // 🔥 INIT VIEWER WITH PANEL
        viewer = JmolViewer.allocateViewer(renderPanel, null);

        viewer.evalString("background black;");
        viewer.evalString("set antialiasDisplay true;");
    }

    public void loadProtein(String pdbId) {

        if (pdbId == null || pdbId.isEmpty()) return;

        System.out.println("Loading PDB: " + pdbId);

        viewer.evalString("zap;");
        viewer.evalString("load https://files.rcsb.org/download/" + pdbId + ".pdb;");

        viewer.evalString("select all;");
        viewer.evalString("cartoon;");
        viewer.evalString("color structure;");
        viewer.evalString("spacefill off;");
        viewer.evalString("wireframe off;");
        viewer.evalString("zoom 120;");
        viewer.evalString("rotate y 20;");

        // 🔥 FORCE REPAINT LOOP
        repaint();
    }
}