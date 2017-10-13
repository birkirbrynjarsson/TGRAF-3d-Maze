
#ifdef GL_ES
precision mediump float;
#endif

attribute vec3 a_position;
attribute vec3 a_normal;

uniform mat4 u_modelMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_projectionMatrix;

uniform vec4 u_lightPosition;
uniform vec4 u_eyePosition;
uniform vec4 u_lightDiffuse;
uniform vec4 u_materialDiffuse;

uniform float u_materialShininess;

//uniform vec4 u_color;
varying vec4 v_color;

void main()
{
	vec4 position = vec4(a_position.x, a_position.y, a_position.z, 1.0);
	position = u_modelMatrix * position;

	vec4 normal = vec4(a_normal.x, a_normal.y, a_normal.z, 0.0);
	normal = u_modelMatrix * normal;

    // --- Global coordinates ---

    // Lighting

    vec4 s = u_lightPosition - position; // Vector pointing to the light
    vec4 v = u_eyePosition - position; // Vector pointing to the camera

    vec4 h = s + v;

    float lambert = dot(normal, s) / (length(normal) * length(s)); // How light hits the objects
    float phong = dot(normal, h) / (length(normal) * length(h));

    v_color = lambert * u_lightDiffuse * u_materialDiffuse; //+ pow(phong, u_materialShininess) * u_lightDiffuse * vec4(0.1f,0.1f,0.1f,1f);

	position = u_viewMatrix * position;
	//normal = u_viewMatrix * normal;

	// --- Eye coordinates ---

    //v_color = (max(0, (dot(normal, normalize(vec4(-position.x, -position.y, -position.z,0)) / length(normal)))) * u_color);
	//v_color = (dot(normal, vec4(0,0,1,0)) / length(normal)) * u_color;

	gl_Position = u_projectionMatrix * position;
}