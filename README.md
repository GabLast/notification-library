# Notification Library

### Tech Stack / Dependencias

1. Java v21
2. Maven
3. Lombok
4. Google Libphonenumber

### Instalación

1. Descargar e instalar Java version 21
2. Descargar e instalar Maven version 3.9.11 o mayor
3. Correr tests: Ir al root del proyecto y digitar ``` mvn clean test ```
4. Correr Libreria: Ir al root del proyecto y digitar ``` mvn compile exec:java ```

### Quick Start

#### Ejemplo base en main -> java -> com.example.notifications -> Main

1. Instanciar los servicios

```
MessageService messageService = MessageServiceImpl.getInstance();
ChannelService channelService = ChannelServiceImpl.getInstance();
ProviderService providerService = ProviderServiceImpl.getInstance();
NotificationService notificationService = NotificationServiceImpl.getInstance();
ChannelProviderService channelProviderService = ChannelProviderServiceImpl.getInstance();
```

2. Crear un template para el mensaje a enviar

```
Message message = messageService.createMessage(Message.builder().subject("Subject #1").message("Message body #0").build())
```

3. Crear / buscar un provider (Proveedores Disponibles en startup: Twilio, Mailgun)

```
Provider twilio = providerService.findProviderByName(
                Provider.ProviderTypes.TWILIO.getName());
```

```
Provider newProvider = providerService.addProvider(
                Provider.builder().name("New Provider").build());
```

4. Buscar un canal soportado (SMS, E-mail, Push Notification, Slack)

```
Channel email = channelService.findChannelByName(Channel.ChannelType.EMAIL.getName());
```

5. Crear configuración con Api Key o Webhook dependiendo del canal

```
ChannelProvider twilioMail = channelProviderService.configureChannelProvider(email, twilio,
                        CommonUtils.generateApiKey(), "https://hooks.slack.com/services/123key");
```

6. Enviar notificacion

- messageCreator: String - Requerido - Identidad origen del mensaje. Correo si es Canal E-mail, un identificador en
  otros casos
- receiver: String - Requerido - Receptor del mensaje.
  Si es canal E-mail, pueden ser varios correos separados por comas.
  Si es canal SMS, pueden ser varios números de teléfonos separados por comas.
  Cualquier otro identificador para Slack / Push notification (user IDs, chat team ID....)
- message - Message - Requerido - Mensaje Template a enviar.
- cc - String - Opcional - Correos copias para E-mails
- configuracion - ChannelProvider - Requerido - Configuración realizada entre proveedor y canal
- forceError - boolean - Permite emular un error de envío de notificación
- notificationRetry - Notificacion - Opcional - Referencia para actualizar la notificación en caso de haber fallado en
  una ejecución previa

```
notificationService.sendNotification(messageCreator, receiver, cc, message, configuracion, forceError, null))
```

### Configuración

1. Crear / buscar un provider (Proveedores Disponibles en startup: Twilio, Mailgun)

```
Provider twilio = providerService.findProviderByName(
                Provider.ProviderTypes.TWILIO.getName());
```

```
Provider newProvider = providerService.addProvider(
                Provider.builder().name("New Provider").build());
```

2. Buscar un canal soportado (SMS, E-mail, Push Notification, Slack)

```
Channel email = channelService.findChannelByName(Channel.ChannelType.EMAIL.getName());
```

3. Crear configuración con Api Key o Webhook dependiendo del canal

```
ChannelProvider twilioMail = channelProviderService.configureChannelProvider(email, twilio,
                        CommonUtils.generateApiKey(), "https://hooks.slack.com/services/123key");
```

### Proveedores Soportados

Por defecto se tienen a Twilio y a Mailgun. Se puede agregar un nuevo proveedor como se muestra a continuación:

```
Provider newProvider = providerService.addProvider(Provider.builder().name("New Provider").build());
```

#### Debe validar la documentación del proveedor para ver si dicho proveedor tiene una integración para los canales disponibles. Ej: "New Provider" no tiene una integración con el canal "Slack", por lo que no debería ser agregado como una nueva configuración.

### API Reference

1. NotificationService

*batchSendNotificationsToConfiguredChannelProviders* Envia un listado de templates de mensajes a las integraciones de
canales-proveedores configuradas.
En caso de no tener configuraciones hechas y enviar configuredProviders como null, se usan las generadas internamente
en la librería

```
batchSendNotificationsToConfiguredChannelProviders(
List<Message> messages,
List<ChannelProvider> configuredProviders)
```

*sendNotification* Función base para enviar alguna notificación hacia algún canal mediante algun proveedor.

```
notificationService.sendNotification(messageCreator, receiver, cc, message, configuracion, forceError, null))
```

2. ChannelProviderService

*configureChannelProvider* Crea la configuración a utilizar para enviar la notificación mediante un proveedor

```
ChannelProvider twilioMail = channelProviderService.configureChannelProvider(email, twilio,
                        CommonUtils.generateApiKey(), "https://hooks.slack.com/services/123key");
```

3. MessageService

*createMessage* Crea un template para el mensaje que será notificado

```
Message message = messageService.createMessage(Message.builder().subject("Subject #1")
.contentType("text/plain")
.message("Message body #0").build())
```

### Seguridad

1. API Keys / Webhooks dependerán del proveedor.
2. EVITAR GUARDAR TEMPLATES DE MENSAJES CON LAS CREDENCIALES DEL PROVEEDOR. El Message template es solo para guardar
   los mensajes que se piensan enviar. Cualquier configuración solo se guarda en ChannelProvider mediante
   ChannelProviderService.