#ifdef GL_ES
precision mediump float;
#endif

uniform vec4 u_lightDiffuse;
uniform vec4 u_materialDiffuse;

varying vec4 v_s;
varying vec4 v_h;
varying vec4 v_n;

void main()
{
    float lambert = dot(v_n, v_s) / (length(v_n) * length(v_s)); // How light hits the objects
    float phong = dot(v_n, v_h) / (length(v_n) * length(v_h));

    vec4 color = lambert * u_lightDiffuse * u_materialDiffuse;// + pow(phong, u_materialShininess) * u_lightDiffuse * vec4(0.1f,0.1f,0.1f,1f);

	gl_FragColor = color;
}