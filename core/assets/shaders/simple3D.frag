#ifdef GL_ES
precision mediump float;
#endif

uniform vec4 u_lightDiffuse;
uniform vec4 u_materialDiffuse;
uniform float u_materialShininess;

varying vec4 v_s;
varying vec4 v_n;

void main()
{
    float lambert = dot(v_n, v_s) / (length(v_n) * length(v_s)); // How light hits the objects

    vec4 color = (lambert * u_lightDiffuse * u_materialDiffuse);


	gl_FragColor = color;
}