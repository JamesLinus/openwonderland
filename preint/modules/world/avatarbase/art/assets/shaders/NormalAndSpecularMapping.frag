varying vec3 ToLight; // Normalized vector to the light
varying vec3 ToCamera; // Normalized direction to the viewer
varying vec3 position;

uniform sampler2D   DiffuseMapIndex;
uniform sampler2D   NormalMapIndex;
uniform sampler2D   SpecularMapIndex;

uniform float SpecularPower;

void main(void)
{
    	// Multi tex coords are screwed up for now.... default to index 0
    	vec4 texColor      = texture2D(DiffuseMapIndex, gl_TexCoord[0].st);
    	vec4 specColor     = texture2D(SpecularMapIndex, gl_TexCoord[0].st);
    	vec4 normalMapValue  = texture2D(NormalMapIndex, gl_TexCoord[0].st);


    	vec3 normal = normalize(normalMapValue.xyz * 2.0 - 1.0);
 
	vec3 lightVector = normalize(ToLight); 

	vec3 camVector = normalize(ToCamera);

	vec3 reflection = normalize(reflect(lightVector * -1.0, normal));
         
  	float nxDir = max(0.0, dot(normal, lightVector));
  
  	vec4 diffuse = texColor * (gl_LightSource[0].diffuse * nxDir);

	vec4 specular = (gl_LightSource[0].specular * pow(max(dot(reflection, camVector),0.0), SpecularPower));
	specular *= ceil(nxDir);
	specular *=  specColor;

	vec4 color = diffuse * 0.85 + texColor * 0.15 + specular;

    	//gl_FragColor = vec4(camVector, 1.0);
	//gl_FragColor = specColor;
    	gl_FragColor = color;
	//gl_FragColor = diffuse;
}