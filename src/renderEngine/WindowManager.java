package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.*;

public class WindowManager {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int FPS_CAP = 120;

    public static void init() {
        ContextAttribs ca = new ContextAttribs(4, 5)
                .withForwardCompatible(true)
                .withProfileCore(true);

        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat(), ca);
            Display.setTitle("Ribice");
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        // Nastavimo kje v displayu bo opengl kontekst
        GL11.glViewport(0, 0, WIDTH, HEIGHT);
    }

    public static void update() {
        Display.sync(FPS_CAP);
        Display.update();
    }

    public static void destroy() { Display.destroy(); }
}
