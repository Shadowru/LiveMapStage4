uniform mat4 u_Matrix;
uniform mat4 m_Matrix;

attribute vec4 a_Position;

varying float diffuseFactor;

attribute vec2 a_texCoord;
varying vec2 v_texCoord;

void main(void)
{

    diffuseFactor = 0.3;

    gl_Position = u_Matrix * m_Matrix * a_Position;
    v_texCoord = a_texCoord;
}
