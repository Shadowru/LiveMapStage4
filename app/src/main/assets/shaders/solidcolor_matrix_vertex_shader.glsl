uniform mat4 u_Matrix;
uniform mat4 m_Matrix;
attribute vec4 a_Position;
attribute vec3 a_Normal;
uniform vec3 u_LightPos;
varying float diffuseFactor;

void main() {

    vec4 modelViewVertex2 = u_Matrix * a_Position;

    vec3 modelViewVertex = vec3(modelViewVertex2);

    vec3 lightVector = normalize(u_LightPos - modelViewVertex);

    vec3 modelViewNormal = vec3(u_Matrix * vec4(a_Normal, 0.0));

    float diffuse = max(dot(modelViewNormal, lightVector), 0.1);
    float distance = length(u_LightPos - modelViewVertex);

    diffuse = diffuse * (1.0 / (1.0 + (0.03 * distance * distance)));
    diffuse = diffuse + 0.1;

    diffuseFactor = diffuse;

    gl_Position = u_Matrix * model_Matrix * a_Position;
    gl_PointSize = 15.0;
}