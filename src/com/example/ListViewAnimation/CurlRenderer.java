/*
   Copyright 2012 Harri Smatt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.example.ListViewAnimation;

import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Actual renderer class.
 *
 * @author harism
 */
public class CurlRenderer implements GLSurfaceView.Renderer {

    // Constant for requesting left page rect.
    public static final int PAGE_LEFT = 1;
    // Constant for requesting right page rect.
    public static final int PAGE_RIGHT = 2;
    // Constants for changing view mode.
    public static final int SHOW_ONE_PAGE = 1;
    public static final int SHOW_TWO_PAGES = 2;
    // Set to true for checking quickly how perspective projection looks.
    // private static final boolean USE_PERSPECTIVE_PROJECTION = false;

    private CurlMesh mesh;

    private boolean begin;

    private RectF mMargins = new RectF();
    // Page rectangles.
    // Projection matrix.
    private final float[] mProjectionMatrix = new float[16];
    // Shaders.
    private final CurlShader mShaderShadow = new CurlShader();
    private final CurlShader mShaderTexture = new CurlShader();
    // View mode.
    private int mViewMode = SHOW_ONE_PAGE;
    // Screen size.
    private int mViewportWidth, mViewportHeight;
    // Rect for render area.
    private final RectF mViewRect = new RectF();

    private final RectF mPageRect = new RectF();

    // Shaders.
    private final String SHADER_SHADOW_FRAGMENT =
            "precision mediump float;\n" +
                    "varying vec4 vColor;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = vColor;\n" +
                    "}\n";
    private final String SHADER_SHADOW_VERTEX =
            "uniform mat4 uProjectionM;\n" +
                    "attribute vec3 aPosition;\n" +
                    "attribute vec4 aColor;\n" +
                    "varying vec4 vColor;\n" +
                    "void main() {\n" +
                    "  gl_Position = uProjectionM * vec4(aPosition, 1.0);\n" +
                    "  vColor = aColor;\n" +
                    "}\n";
    private final String SHADER_TEXTURE_FRAGMENT =
            "precision mediump float;\n" +
                    "varying vec4 vColor;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform sampler2D sTexture;\n" +
                    "void main() {\n" +
//                    "gl_FragColor=vColor;\n" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "  gl_FragColor.rgb *= vColor.rgb;\n" +
                    "  gl_FragColor = mix(vColor, gl_FragColor, vColor.a);\n" +
                    "  gl_FragColor.a = 1.0;\n" +
                    "}\n";
    private final String SHADER_TEXTURE_VERTEX =
            "uniform mat4 uProjectionM;\n" +
                    "attribute vec3 aPosition;\n" +
                    "attribute vec4 aColor;\n" +
                    "attribute vec2 aTextureCoord;\n" +
                    "varying vec4 vColor;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = uProjectionM * vec4(aPosition, 1.0);\n" +
                    "  vColor = aColor;\n" +
                    "  vTextureCoord = aTextureCoord;\n" +
                    "}\n";

    /**
     * Basic constructor.
     */
    public CurlRenderer() {
        this.mesh = new CurlMesh(10);
    }

    private float positionFactor = 1;

    public void setPositionFactor(float positionFactor) {
        this.positionFactor = positionFactor;

    }

    @Override
    public synchronized void onDrawFrame(GL10 unused) {
//        GLES20.glClearColor(0f, 0f, 0f, 0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        float ratio = mViewportWidth / mViewportHeight;

        if (this.mesh != null) {
            Log.d("CurlMesh.Blink", "curl renderer on draw frame, position factor: " + positionFactor + ", ratio: " + ratio);

            mesh.setRect(this.mViewRect);
            mesh.reset();
            mesh.curl(new PointF(positionFactor * ratio * 2, positionFactor), new PointF((float) (.9f + .1f * (positionFactor + 1) / 2), 1f), .8);
            mesh.onDrawFrame(mShaderTexture, mShaderShadow);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mViewportWidth = width;
        mViewportHeight = height;

        float ratio = (float) width / height;
        mViewRect.top = 1.0f;
        mViewRect.bottom = -1.0f;
        mViewRect.left = -ratio;
        mViewRect.right = ratio;
        updatePageRects();

        Matrix.orthoM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, -10f, 10f);
        mShaderTexture.useProgram();
        GLES20.glUniformMatrix4fv(mShaderTexture.getHandle("uProjectionM"), 1, false, mProjectionMatrix, 0);
        mShaderShadow.useProgram();
        GLES20.glUniformMatrix4fv(mShaderShadow.getHandle("uProjectionM"), 1, false, mProjectionMatrix, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        try {
            mShaderShadow.setProgram(SHADER_SHADOW_VERTEX, SHADER_SHADOW_FRAGMENT);
            mShaderTexture.setProgram(SHADER_TEXTURE_VERTEX, SHADER_TEXTURE_FRAGMENT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public CurlMesh getMesh() {
        return mesh;
    }

    /**
     * Set margins or padding. Note: margins are proportional. Meaning a value
     * of .1f will produce a 10% margin.
     */
    public synchronized void setMargins(float left, float top, float right,
                                        float bottom) {
        mMargins.left = left;
        mMargins.top = top;
        mMargins.right = right;
        mMargins.bottom = bottom;
        updatePageRects();
    }

    /**
     * Recalculates page rectangles.
     */
    private void updatePageRects() {
        mPageRect.set(mViewRect);
        mPageRect.left += mViewRect.width() * mMargins.left;
        mPageRect.right -= mViewRect.width() * mMargins.right;
        mPageRect.top += mViewRect.height() * mMargins.top;
        mPageRect.bottom -= mViewRect.height() * mMargins.bottom;

        Log.d("CurlRender", ">>>update page rects: " + mPageRect);
    }
}
