precision mediump float;
uniform vec4 u_Color;
varying float diffuseFactor;

void main() {

    gl_FragColor = u_Color * diffuseFactor;
}