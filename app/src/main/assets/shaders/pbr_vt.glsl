uniform mat4 u_Matrix;
uniform mat4 m_Matrix;

attribute vec4 a_Position;
attribute vec3 a_Normal;

uniform vec3 u_LightPos;

varying float diffuseFactor;

attribute vec2 a_texCoord;
varying vec2 v_texCoord;

void main(void)
{

    vec3 modelViewVertex = vec3(m_Matrix * a_Position);
    vec3 lightVector = normalize(u_LightPos - modelViewVertex);
    vec3 modelViewNormal = vec3(m_Matrix * vec4(a_Normal, 0.0));

    float diffuse = max(dot(modelViewNormal, lightVector), 0.2);
    float distance = length(u_LightPos - modelViewVertex);
    diffuse = diffuse * (1.0 / (1.0 + (0.0000025 * distance * distance)));
	diffuseFactor = diffuse + 0.4;

    gl_Position = u_Matrix * m_Matrix * a_Position;
    v_texCoord = a_texCoord;
}