# De l'injection de méthodes dans Spring à l'aide de Spring AOP

> Spring framework propose depuis sa version 1 le concept d'injection de méthode. Celui-ci a cependant été peu mis en avant depuis et ne semble pouvoir être mis en oeuvre qu'à travers une définition xml du contexte Spring ou via l'API BeanDefinition : on n'est donc pas très emballé ! Les éditeurs du framework souhaitent-ils vraiment pousser à l'utilisation de cette fonctionnalité ? Et pourtant certains autres projets du framework l'utilisent. A travers un exemple concret où l'injection de méthode pourrait s'avérer bien utile, nous verrons comment nous pouvons mettre en oeuvre ce concept à l'aide de Spring AOP.

1. Spring AOP
  - Ce n'est pas
  - pgm orientée aspect
  - Proxy
3. Use case
  - Api contract first
  - implémentation controlleur identiques
4. Injection de méthode
  - Injection de méthode Spring dans les interfaces générées de controlleurs
    AbstractBeanDefinition -> MethodOverride -> ReplaceOverride -> MethodReplacer 
  - Extension impossible !
5. Application de Spring AOP
  - Comme Spring Data, Spring security,...
  - Code
6. Ouverture
  - Annotation ?
  - Autres cas d'usage
  - Autres solutions (génération de code avec api tools generator)
