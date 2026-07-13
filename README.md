Pasos para ejecutar el proyecto correctamente:

1.Abrir en Intellij el proyecto Boletera-Backend 
Rellenar el application.propiertes con lo siguiente: 

# Cloudinary
cloudinary.cloud_name=dnf8jpcu1
cloudinary.api_key=471766721845585
cloudinary.api_secret=1ufu9-syFOQPO5kEXZuBbH2eqe0

Esta sera la cuenta con la que se ingresara como administrador al levantar el proyecto boletera-backend.

# Seguridad
app.jwt.secret=jLCyF1BYw9Um738XMcsPlpJQoKfavTg52db6iHhxVkOR4tZIGWuzArnS0qeNED
app.admin.email=admin@ticketplace.com  -- correo
app.admin.password=Admin123!  -- contrasenia

2.Abrir en VisualStudio el proyecto Boletera-cliente

Una vez levantado el backend en Intellij, abrir el proyecto Boletera-Cliente en VS y ejecutar el index.html con la extension de Live Server.


El backend en el puerto localhost8080 sera para iniciar sesion como administrador y acceder al panel administrativo.

La vista de usuario final se accedera a traves del index.html que se encuentra en el proyecto Boletera-Cliente mediante la extension live server, una vez levantado
tendra que registrar una cuenta para luego iniciar sesion con ella y poder acceder.
