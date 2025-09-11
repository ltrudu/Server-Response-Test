# Contributing Guide

Thank you for your interest in contributing to Server Response Test! This guide will help you get started with contributing code, documentation, translations, and more.

## 🤝 Ways to Contribute

### 🐛 Bug Reports
- Report issues you encounter
- Provide detailed reproduction steps
- Include device and version information

### 💡 Feature Requests  
- Suggest new functionality
- Describe use cases and benefits
- Provide mockups or examples when helpful

### 🔧 Code Contributions
- Fix bugs and implement features
- Improve performance and code quality
- Add unit tests and documentation

### 📚 Documentation
- Improve existing documentation
- Add examples and tutorials
- Fix typos and clarify instructions

### 🌍 Translations
- Add support for new languages
- Improve existing translations
- Maintain localization quality

## 🚀 Getting Started

### Prerequisites

Before contributing, ensure you have:
- **Android Studio**: Arctic Fox or later
- **JDK**: Version 8 or higher
- **Git**: For version control
- **GitHub Account**: For pull requests

### Development Setup

1. **Fork the Repository**
   ```bash
   # Fork on GitHub, then clone your fork
   git clone https://github.com/YOUR_USERNAME/Server-Response-Test.git
   cd Server-Response-Test
   ```

2. **Add Upstream Remote**
   ```bash
   git remote add upstream https://github.com/ltrudu/Server-Response-Test.git
   ```

3. **Set Up Development Environment**
   - Open project in Android Studio
   - Sync Gradle dependencies
   - Run tests to verify setup

4. **Create Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

## 📋 Contribution Workflow

### 1. Planning Your Contribution

#### For Bug Fixes
- Check if issue already exists
- Create issue if not reported
- Discuss approach in issue comments
- Get approval before major changes

#### For New Features
- Open feature request issue first
- Discuss requirements and design
- Wait for maintainer approval
- Break large features into smaller PRs

### 2. Development Process

#### Code Standards
- Follow existing code style and patterns
- Use meaningful variable and method names
- Add comments for complex logic
- Follow Android development best practices

#### Testing Requirements
- Add unit tests for new functionality
- Update existing tests if needed
- Ensure all tests pass locally
- Test on multiple Android versions when possible

#### Documentation Updates
- Update relevant documentation
- Add code comments for public APIs
- Update README if needed
- Include screenshots for UI changes

### 3. Submitting Changes

#### Commit Guidelines
```bash
# Good commit messages
git commit -m "Fix notification permission check for Android 13+"
git commit -m "Add French translations for notification dialogs"
git commit -m "Improve HTTPS testing error handling"

# Include more details in body if needed
git commit -m "Fix race condition in import synchronization

- Add callback mechanism for database operations
- Implement timeout protection for import process
- Add comprehensive logging for debugging
- Resolves #123"
```

#### Pull Request Process
1. **Push your branch**
   ```bash
   git push origin feature/your-feature-name
   ```

2. **Create Pull Request**
   - Use descriptive title
   - Fill out PR template completely
   - Reference related issues
   - Add screenshots for UI changes

3. **PR Review Process**
   - Maintainers will review code
   - Address feedback promptly
   - Make requested changes
   - Keep discussion professional

## 🏗️ Code Architecture

### Project Structure
```
app/src/main/java/com/ltrudu/serverresponsetest/
├── data/           # Database entities and DAOs
├── repository/     # Data access layer
├── viewmodel/      # UI state management  
├── fragment/       # UI fragments
├── adapter/        # RecyclerView adapters
├── service/        # Background services
└── ui/            # Custom UI components
```

### Key Architectural Patterns

#### MVVM Implementation
- **Views**: Fragments handle UI interactions
- **ViewModels**: Manage UI state and business logic
- **Models**: Data entities and repository pattern

#### Database Layer
- **Room**: SQLite abstraction with type safety
- **DAOs**: Data access interfaces
- **Entities**: Database table representations

#### Service Architecture
- **Foreground Service**: Background testing execution
- **Broadcast Receivers**: Inter-component communication
- **Notification System**: Rich background notifications

### Coding Standards

#### Java Style Guide
```java
// Use meaningful names
public class ServerTestService extends Service {
    private static final int NOTIFICATION_ID = 1001;
    private boolean isTestRunning = false;
    
    // Document public methods
    /**
     * Starts server testing with specified configuration
     * @param servers List of servers to test
     * @param settings Test configuration parameters
     */
    public void startTesting(List<Server> servers, Settings settings) {
        // Implementation
    }
}
```

#### Resource Naming
```xml
<!-- strings.xml -->
<string name="test_start_button">Start Test</string>
<string name="notification_permission_title">Enable Notifications</string>

<!-- layouts -->
fragment_test.xml
item_server_result.xml

<!-- drawables -->
ic_play_24.xml
ic_notification_server_test.xml
```

## 🧪 Testing Guidelines

### Unit Testing
- Test business logic in ViewModels
- Test data operations in Repository
- Test utility functions and helpers
- Mock dependencies appropriately

### Integration Testing
- Test database operations
- Test service functionality
- Test fragment interactions
- Test notification systems

### Manual Testing
- Test on multiple device types
- Test different Android versions
- Test with different network conditions
- Test background/foreground scenarios

### Testing Checklist
- [ ] All existing tests pass
- [ ] New functionality has tests
- [ ] Manual testing completed
- [ ] No memory leaks introduced
- [ ] Performance impact assessed

## 🌍 Localization Contributions

### Adding New Languages

1. **Create Language Directory**
   ```
   app/src/main/res/values-{language_code}/
   ```

2. **Translate Strings**
   ```xml
   <!-- values-es/strings.xml (Spanish example) -->
   <resources>
       <string name="app_name">Prueba de Respuesta del Servidor</string>
       <string name="test_start_button">Iniciar Prueba</string>
       <!-- ... all other strings -->
   </resources>
   ```

3. **Test Translation**
   - Change device language
   - Verify all text displays correctly
   - Check for text overflow issues
   - Test with longer/shorter translations

### Translation Guidelines
- **Maintain context**: Understand feature context
- **Keep consistency**: Use consistent terminology
- **Test thoroughly**: Verify UI layout works
- **Cultural adaptation**: Consider cultural differences

### Supported Languages
- **English** (en) - Default
- **French** (fr) - Complete
- **Your language** - Contribute!

## 📝 Documentation Contributions

### Types of Documentation
- **User guides**: How-to instructions
- **Technical docs**: Architecture and APIs
- **Code comments**: Inline documentation
- **Wiki pages**: Comprehensive guides

### Documentation Standards
- **Clear structure**: Use headings and sections
- **Practical examples**: Include code samples
- **Screenshots**: Add visuals for UI features
- **Cross-references**: Link related topics

### Wiki Contributions
```markdown
# Page Title

Brief introduction explaining the purpose.

## Section Heading

Content with examples:

```java
// Code example
public void exampleMethod() {
    // Clear comments
}
```

### Subsection
- Use lists for steps
- Include screenshots
- Link to related pages
```

## 🔍 Code Review Process

### What Reviewers Look For

#### Code Quality
- Follows established patterns
- Proper error handling
- Memory leak prevention
- Performance considerations

#### Testing
- Adequate test coverage
- Tests actually test the code
- No flaky tests
- Manual testing documented

#### Documentation
- Clear commit messages
- Updated documentation
- Code comments where needed
- PR description complete

### Review Response Guidelines
- **Be responsive**: Address feedback promptly
- **Ask questions**: If feedback is unclear
- **Explain decisions**: When disagreeing with feedback
- **Learn from feedback**: Use reviews to improve

## 🏷️ Issue Management

### Bug Report Template
```markdown
**Bug Description**
Clear description of the issue

**Steps to Reproduce**
1. Step one
2. Step two
3. Step three

**Expected Behavior**
What should happen

**Actual Behavior**
What actually happens

**Environment**
- Device: Samsung Galaxy S21
- Android Version: 12
- App Version: 1.1

**Additional Information**
Logs, screenshots, etc.
```

### Feature Request Template
```markdown
**Feature Description**
Clear description of the proposed feature

**Use Case**
Why is this feature needed?

**Proposed Solution**
How should it work?

**Alternatives Considered**
Other approaches you've thought about

**Additional Context**
Mockups, examples, etc.
```

## 🎯 Contribution Guidelines

### Do's ✅
- **Follow the workflow**: Branch → Develop → Test → PR
- **Communicate early**: Discuss large changes before implementing
- **Write tests**: Cover new functionality with tests
- **Update documentation**: Keep docs in sync with code
- **Be patient**: Reviews take time, especially for large changes

### Don'ts ❌
- **Don't force push**: To shared branches
- **Don't ignore feedback**: Address review comments
- **Don't break tests**: Ensure all tests pass
- **Don't submit huge PRs**: Break large changes into smaller pieces
- **Don't assume**: Ask questions if unclear

### Code of Conduct
- **Be respectful**: Treat everyone with respect
- **Be constructive**: Provide helpful feedback
- **Be patient**: Remember we're all volunteers
- **Be inclusive**: Welcome newcomers and diverse perspectives

## 🚀 Release Process

### Version Numbering
- **Major.Minor.Patch** (e.g., 1.1.0)
- **Major**: Breaking changes or major features
- **Minor**: New features, backwards compatible
- **Patch**: Bug fixes and minor improvements

### Release Timeline
- **Feature freeze**: 1 week before release
- **Testing period**: Comprehensive testing
- **Release candidate**: Final testing
- **Release**: Tagged and published

## 📞 Getting Help

### Communication Channels
- **GitHub Issues**: Bug reports and feature requests
- **GitHub Discussions**: General questions and ideas
- **Code Reviews**: Feedback on pull requests
- **Email**: support@trudu.com for private concerns

### Resources
- **[Architecture Guide](Architecture.md)**: Understand the codebase
- **[User Guide](User-Guide.md)**: Learn how the app works
- **[API Reference](API-Reference.md)**: Code documentation
- **[Development Setup](Development-Setup.md)**: Environment configuration

---

## 🎉 Recognition

Contributors are recognized in several ways:
- **Commit attribution**: Your commits in git history
- **Release notes**: Mentioned in changelog
- **Contributors list**: Added to project contributors
- **Maintainer status**: For regular contributors

Thank you for contributing to Server Response Test! Your efforts help make this tool better for network monitoring professionals worldwide. 🚀

---

**Questions?** Don't hesitate to ask in [GitHub Issues](https://github.com/ltrudu/Server-Response-Test/issues) or create an issue for clarification.