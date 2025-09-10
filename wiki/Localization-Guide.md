# Localization Guide

This comprehensive guide covers all aspects of internationalization and localization for the Server Response Test application, including adding new languages, maintaining translations, and best practices.

## 🌍 Overview

### Current Language Support
- **🇺🇸 English** (en) - Default language, complete
- **🇫🇷 French** (fr) - Complete translation, including v1.1 features

### Localization Goals
- **User Accessibility**: Make app available to non-English speakers
- **Cultural Adaptation**: Respect local conventions and preferences
- **Consistent Experience**: Maintain functionality across all languages
- **Quality Translations**: Accurate, contextual, and natural translations

## 📋 Getting Started with Translation

### Prerequisites for Translators

#### Technical Requirements
- **Text Editor**: Any UTF-8 capable editor (Android Studio, VS Code, Notepad++)
- **XML Knowledge**: Basic understanding of XML structure
- **GitHub Account**: For contributing translations
- **Git Basics**: For submitting translations (or ask for help)

#### Language Requirements
- **Native or Fluent**: In target language
- **Technical Vocabulary**: Understanding of mobile app and networking terms
- **Context Awareness**: Ability to understand feature context
- **Android Experience**: Familiarity with Android app conventions (helpful)

### Translation Workflow

#### Step 1: Setup
```bash
# Fork the repository
# Clone your fork
git clone https://github.com/YOUR_USERNAME/ServerResponseTest.git
cd ServerResponseTest

# Create feature branch
git checkout -b feature/localization-spanish
```

#### Step 2: Create Language Directory
```bash
# Create values directory for your language
# Language codes: https://developer.android.com/guide/topics/resources/providing-resources#AlternativeResources

mkdir -p app/src/main/res/values-es/  # Spanish
mkdir -p app/src/main/res/values-de/  # German
mkdir -p app/src/main/res/values-ja/  # Japanese
mkdir -p app/src/main/res/values-pt/  # Portuguese
```

#### Step 3: Copy and Translate
```bash
# Copy base strings file
cp app/src/main/res/values/strings.xml app/src/main/res/values-es/strings.xml

# Edit the new file with your translations
# Keep the same XML structure, only translate the text content
```

## 📝 Translation Guidelines

### XML Structure Rules

#### Keep XML Tags Unchanged
```xml
<!-- ✅ Correct -->
<string name="app_name">Prueba de Respuesta del Servidor</string>
<string name="test_start_button">Iniciar Prueba</string>

<!-- ❌ Wrong - Don't change the name attribute -->
<string name="nombre_app">Prueba de Respuesta del Servidor</string>
```

#### Preserve Placeholders
```xml
<!-- ✅ Correct - Keep %s, %d, etc. placeholders -->
<string name="remaining_requests">Solicitudes restantes: %d/%d</string>
<string name="server_test_result">Servidor %s: %s en %dms</string>

<!-- ❌ Wrong - Don't remove or change placeholders -->
<string name="remaining_requests">Solicitudes restantes: número/número</string>
```

#### Handle Special Characters
```xml
<!-- ✅ Correct - Escape special characters -->
<string name="dialog_message">¿Está seguro de que desea eliminar \"%%s\"?</string>
<string name="apostrophe_example">No se puede conectar</string>

<!-- ❌ Wrong - Unescaped characters -->
<string name="dialog_message">¿Está seguro de que desea eliminar "%s"?</string>
```

### Translation Quality Guidelines

#### Contextual Translation
```xml
<!-- Example: "Test" can mean different things -->

<!-- In the context of running tests -->
<string name="test_tab_title">Prueba</string>           <!-- Spanish -->
<string name="test_tab_title">Test</string>             <!-- German -->

<!-- In the context of a test server -->
<string name="test_server_name">Servidor de Prueba</string>    <!-- Spanish -->
<string name="test_server_name">Test-Server</string>           <!-- German -->
```

#### Maintain Consistency
```xml
<!-- Use consistent terminology throughout -->
<string name="server_list_title">Lista de Servidores</string>
<string name="add_server_button">Agregar Servidor</string>
<string name="edit_server_title">Editar Servidor</string>
<string name="delete_server_confirmation">¿Eliminar servidor?</string>

<!-- Don't mix synonyms -->
<!-- ❌ Wrong: mixing "servidor" and "equipo" for the same concept -->
```

#### Cultural Adaptation
```xml
<!-- Adapt to local conventions -->
<!-- Time formats -->
<string name="time_format_12h">h:mm a</string>        <!-- US English -->
<string name="time_format_24h">HH:mm</string>         <!-- Most other languages -->

<!-- Date formats -->
<string name="date_format_us">MM/dd/yyyy</string>     <!-- US English -->
<string name="date_format_eu">dd/MM/yyyy</string>     <!-- European -->
<string name="date_format_iso">yyyy-MM-dd</string>    <!-- ISO standard -->
```

## 🔤 Complete Translation Reference

### Core UI Elements

#### Main Navigation
```xml
<!-- Tab titles -->
<string name="tab_test">Test</string>                     <!-- en -->
<string name="tab_test">Prueba</string>                   <!-- es -->
<string name="tab_test">Test</string>                     <!-- de -->

<string name="tab_server_list">Server List</string>       <!-- en -->
<string name="tab_server_list">Lista de Servidores</string>  <!-- es -->
<string name="tab_server_list">Serverliste</string>       <!-- de -->

<string name="tab_settings">Settings</string>             <!-- en -->
<string name="tab_settings">Configuración</string>       <!-- es -->
<string name="tab_settings">Einstellungen</string>       <!-- de -->
```

#### Test Fragment
```xml
<!-- Test controls -->
<string name="test_start_button">Start Test</string>      <!-- en -->
<string name="test_start_button">Iniciar Prueba</string>  <!-- es -->
<string name="test_start_button">Test starten</string>    <!-- de -->

<string name="test_stop_button">Stop Test</string>        <!-- en -->
<string name="test_stop_button">Detener Prueba</string>   <!-- es -->
<string name="test_stop_button">Test stoppen</string>     <!-- de -->

<!-- Status messages -->
<string name="server_count_display">%d servers configured</string>     <!-- en -->
<string name="server_count_display">%d servidores configurados</string> <!-- es -->
<string name="server_count_display">%d Server konfiguriert</string>     <!-- de -->
```

#### Server Configuration
```xml
<!-- Server form fields -->
<string name="server_name_hint">Server Name</string>      <!-- en -->
<string name="server_name_hint">Nombre del Servidor</string>  <!-- es -->
<string name="server_name_hint">Servername</string>       <!-- de -->

<string name="server_address_hint">Server Address</string> <!-- en -->
<string name="server_address_hint">Dirección del Servidor</string>  <!-- es -->
<string name="server_address_hint">Serveradresse</string>  <!-- de -->

<!-- Request types -->
<string name="request_type_https">HTTPS</string>          <!-- Universal -->
<string name="request_type_ping">Ping</string>            <!-- Universal -->
```

### Notification System

#### Permission Dialogs
```xml
<string name="notification_permission_title">Enable Notifications</string>  <!-- en -->
<string name="notification_permission_title">Activar Notificaciones</string>  <!-- es -->
<string name="notification_permission_title">Benachrichtigungen aktivieren</string>  <!-- de -->

<string name="notification_permission_message">
    This app needs notification permission to show test status while running in the background.
</string>  <!-- en -->

<string name="notification_permission_message">
    Esta aplicación necesita permiso de notificaciones para mostrar el estado de las pruebas mientras se ejecuta en segundo plano.
</string>  <!-- es -->

<string name="notification_permission_message">
    Diese App benötigt Benachrichtigungsberechtigung, um den Teststatus im Hintergrund anzuzeigen.
</string>  <!-- de -->
```

#### Notification Content
```xml
<string name="notification_title">Server Response Test</string>              <!-- Universal -->
<string name="notification_title_testing">Testing %d servers</string>       <!-- en -->
<string name="notification_title_testing">Probando %d servidores</string>   <!-- es -->
<string name="notification_title_testing">Teste %d Server</string>          <!-- de -->

<!-- Notification actions -->
<string name="notification_action_pause">Pause</string>   <!-- en -->
<string name="notification_action_pause">Pausar</string>  <!-- es -->
<string name="notification_action_pause">Pausieren</string>  <!-- de -->

<string name="notification_action_resume">Resume</string>  <!-- en -->
<string name="notification_action_resume">Reanudar</string>  <!-- es -->
<string name="notification_action_resume">Fortsetzen</string>  <!-- de -->
```

### Settings and Configuration

#### Time and Intervals
```xml
<string name="time_between_sessions_label">Time Between Sessions (ms)</string>  <!-- en -->
<string name="time_between_sessions_label">Tiempo Entre Sesiones (ms)</string>  <!-- es -->
<string name="time_between_sessions_label">Zeit zwischen Sitzungen (ms)</string>  <!-- de -->

<string name="infinite_requests_label">Infinite Requests</string>           <!-- en -->
<string name="infinite_requests_label">Solicitudes Infinitas</string>       <!-- es -->
<string name="infinite_requests_label">Unendliche Anfragen</string>          <!-- de -->
```

#### Data Management
```xml
<string name="export_servers_button">Export Servers</string>    <!-- en -->
<string name="export_servers_button">Exportar Servidores</string>  <!-- es -->
<string name="export_servers_button">Server exportieren</string>    <!-- de -->

<string name="import_servers_button">Import Servers</string>    <!-- en -->
<string name="import_servers_button">Importar Servidores</string>  <!-- es -->
<string name="import_servers_button">Server importieren</string>    <!-- de -->

<string name="share_servers_button">Share Servers</string>      <!-- en -->
<string name="share_servers_button">Compartir Servidores</string>  <!-- es -->
<string name="share_servers_button">Server teilen</string>      <!-- de -->
```

### Error Messages and Validation

#### Validation Errors
```xml
<string name="error_server_name_required">Server name is required</string>     <!-- en -->
<string name="error_server_name_required">Se requiere nombre del servidor</string>  <!-- es -->
<string name="error_server_name_required">Servername ist erforderlich</string>  <!-- de -->

<string name="error_invalid_url">Invalid server address</string>              <!-- en -->
<string name="error_invalid_url">Dirección de servidor inválida</string>      <!-- es -->
<string name="error_invalid_url">Ungültige Serveradresse</string>             <!-- de -->
```

#### Network Errors
```xml
<string name="error_network_unavailable">Network unavailable</string>         <!-- en -->
<string name="error_network_unavailable">Red no disponible</string>           <!-- es -->
<string name="error_network_unavailable">Netzwerk nicht verfügbar</string>    <!-- de -->

<string name="error_timeout">Connection timeout</string>                      <!-- en -->
<string name="error_timeout">Tiempo de conexión agotado</string>              <!-- es -->
<string name="error_timeout">Verbindungstimeout</string>                      <!-- de -->
```

## 🧪 Testing Translations

### Testing Process

#### Device Language Testing
```bash
# Test your translation on device/emulator
1. Install app with your translation
2. Go to Settings > System > Languages & input
3. Add your language and set as primary
4. Open app and verify translations
5. Test all major features and flows
6. Check for text overflow or truncation
```

#### Translation Validation Checklist
```
Visual Testing:
├── ✅ All text displays correctly
├── ✅ No text is cut off or truncated
├── ✅ UI elements properly aligned
├── ✅ Buttons and labels readable
├── ✅ Dialog boxes properly sized
├── ✅ Tab titles fit in available space
└── ✅ Notification text displays fully

Functional Testing:
├── ✅ All features work with translated text
├── ✅ Error messages display correctly
├── ✅ Form validation messages clear
├── ✅ Toast messages appropriate length
├── ✅ Placeholder text makes sense
├── ✅ Help text contextually correct
└── ✅ Settings save and load properly
```

#### Common Translation Issues

##### Text Length Problems
```xml
<!-- Problem: German compound words can be very long -->
<!-- Original -->
<string name="notification_permission_title">Enable Notifications</string>

<!-- German translation might be too long for button -->
<string name="notification_permission_title">Benachrichtigungen aktivieren</string>

<!-- Solution: Use shorter alternative -->
<string name="notification_permission_title">Benachrichtigungen</string>
```

##### Context Misunderstanding
```xml
<!-- Problem: "Test" has multiple meanings -->
<!-- Wrong context -->
<string name="test_fragment_title">Examen</string>  <!-- Spanish for "exam" -->

<!-- Correct context -->
<string name="test_fragment_title">Prueba</string>  <!-- Spanish for "test/check" -->
```

## 📱 Platform-Specific Considerations

### Android Localization Features

#### Resource Qualifiers
```
Resource directory structure:
├── values/           (default - English)
├── values-fr/        (French)
├── values-es/        (Spanish)
├── values-de/        (German)
├── values-ja/        (Japanese)
├── values-zh-rCN/    (Simplified Chinese)
├── values-zh-rTW/    (Traditional Chinese)
└── values-ar/        (Arabic - RTL support)
```

#### Right-to-Left (RTL) Support
```xml
<!-- For RTL languages (Arabic, Hebrew, etc.) -->
<!-- Add to AndroidManifest.xml -->
<application
    android:supportsRtl="true">

<!-- Use start/end instead of left/right in layouts -->
<LinearLayout
    android:layout_marginStart="16dp"
    android:layout_marginEnd="16dp">
```

### Text Formatting

#### Pluralization Support
```xml
<!-- English plurals -->
<plurals name="server_count">
    <item quantity="one">%d server</item>
    <item quantity="other">%d servers</item>
</plurals>

<!-- Spanish plurals -->
<plurals name="server_count">
    <item quantity="one">%d servidor</item>
    <item quantity="other">%d servidores</item>
</plurals>

<!-- Usage in code -->
int count = servers.size();
String text = getResources().getQuantityString(R.plurals.server_count, count, count);
```

#### String Formatting
```xml
<!-- Use positional arguments for flexible word order -->
<!-- English -->
<string name="test_result">Server %1$s responded in %2$d ms</string>

<!-- Spanish (different word order) -->
<string name="test_result">El servidor %1$s respondió en %2$d ms</string>

<!-- German (different word order) -->
<string name="test_result">Server %1$s antwortete in %2$d ms</string>
```

## 🚀 Contributing Translations

### Submission Process

#### Small Changes (Direct PR)
```bash
# For small translation updates
1. Fork repository
2. Create translation branch
3. Add/modify strings.xml
4. Test on device
5. Submit pull request
6. Respond to review feedback
```

#### Large Translations (Collaborative)
```bash
# For complete new language
1. Open GitHub issue announcing translation
2. Coordinate with maintainers
3. Work in phases:
   - Core UI (high priority)
   - Advanced features (medium priority)
   - Error messages (lower priority)
4. Submit incremental pull requests
5. Get feedback and iterate
```

### Translation Review Process

#### Quality Assurance
```
Review criteria:
├── Translation accuracy
├── Contextual appropriateness
├── Consistency across app
├── Technical term correctness
├── Cultural sensitivity
├── UI layout impact
└── Functional testing results
```

#### Community Review
```
Review process:
1. Translator submits PR
2. Maintainer does technical review
3. Native speaker reviews accuracy
4. UI/UX review for layout issues
5. Functional testing on device
6. Final approval and merge
```

## 📚 Resources for Translators

### Technical Resources

#### Android Documentation
- [Android Localization Guide](https://developer.android.com/guide/topics/resources/localization)
- [Supporting Different Languages](https://developer.android.com/training/basics/supporting-devices/languages)
- [String Resources](https://developer.android.com/guide/topics/resources/string-resource)

#### Tools and Utilities
- **Android Studio**: IDE with translation support
- **Crowdin**: Translation management platform
- **Lokalise**: Localization platform
- **Google Translate**: Basic translation assistance (review needed)

### Networking Terminology

#### Common Technical Terms
```
English → Spanish → German → French
├── Server → Servidor → Server → Serveur
├── Network → Red → Netzwerk → Réseau
├── Connection → Conexión → Verbindung → Connexion
├── Timeout → Tiempo agotado → Timeout → Délai d'expiration
├── Response → Respuesta → Antwort → Réponse
├── Request → Solicitud → Anfrage → Requête
├── Settings → Configuración → Einstellungen → Paramètres
└── Notification → Notificación → Benachrichtigung → Notification
```

### Style Guidelines by Language

#### Spanish (es)
```
Style preferences:
├── Use formal "usted" form for UI
├── Prefer active voice
├── Use standard Spanish tech terms
├── Avoid anglicisms when possible
└── Use sentence case for buttons
```

#### German (de)
```
Style preferences:
├── Use formal "Sie" form
├── Capitalize nouns appropriately
├── Use compound words judiciously
├── Prefer German tech terms over English
└── Use title case for major headings
```

#### French (fr)
```
Style preferences:
├── Use formal "vous" form
├── Follow French grammar rules
├── Use accent marks correctly
├── Prefer French tech terms
└── Use appropriate gender agreements
```

## 🔄 Maintenance and Updates

### Keeping Translations Current

#### New Feature Translation Workflow
```
When new features are added:
1. Developer adds English strings
2. Mark strings as "needs translation"
3. Notify translation contributors
4. Translators update their language files
5. Review and test translations
6. Include in next release
```

#### Translation Update Process
```bash
# Check for untranslated strings
grep -r "TODO: translate" app/src/main/res/values-*/

# Compare with base English strings
diff app/src/main/res/values/strings.xml app/src/main/res/values-es/strings.xml
```

### Version Control for Translations
```
Best practices:
├── Keep translations in sync with features
├── Use clear commit messages for translation changes
├── Tag translation contributors in relevant issues
├── Maintain translation status documentation
└── Test translations before releases
```

---

## 🤝 Translation Team

### Current Contributors
- **English**: Development team
- **French**: Community contributor
- **[Your Language]**: You could be next!

### Getting Help
- **GitHub Discussions**: Ask questions about translation
- **Issues**: Report translation bugs or suggest improvements
- **Email**: support@trudu.com for translation coordination

### Recognition
Translation contributors are credited in:
- Release notes
- About section (when added)
- Contributors list
- GitHub commit history

Thank you for helping make Server Response Test accessible to users worldwide! 🌍

---

**Ready to contribute?** Check the [Contributing Guide](Contributing.md) for technical details and start translating today!