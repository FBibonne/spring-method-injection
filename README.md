# De l'injection de méthodes dans Spring à l'aide de Spring AOP

---

## A propos de

- Fabrice Bibonne : support aux développeurs sur les technologies java
- Insee :
  - Produit, analyse et diffuse des informations sur l’économie et la société françaises
  - Diffuse également des métadonnées comme les référentiels géographiques à travers des API

---

## A propos de

- POC de la refonte de _Metadata API_ avec approche contract first : 
  - **la spec OAS est une spécification**
  - Eviter une génération laborieuse "du swagger"
  - Documentation fidèle au besoin, au code
  - Implémentation contrainte _as code_

---

## Architecture _Metadata API_



---

## D'abord la spec OAS



---

## Exemple d'interface générée d'un controlleur



---

## Systématiser l'implémentation des controllers Spring

- Fonctionnement d'un controlleur (générique) :
  1. Appeler la bonne requête Sparql
  2. Convertir le résultat dans la bonne entité
  3. La retourner
- ~~Extensions OpenApi Generator~~
- **Générer l'implémentation au runtime (comme Spring Data)**

---

## Génération par "injection de méthode"

- Avec [`MethodReplacer`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/support/MethodReplacer.html) ?
  - Définition xml seulement
  - Peu utilisé
  - Pas compatible avec un contrôle d'accès (`@PreAuthorize`)
- Autre chose ?

---

## Spring AOP !

---

## Spring AOP : ce n'est pas...

![](https://raw.githubusercontent.com/FBibonne/aop-method-injection/aop-method-injection/img/aop.png?token=GHSAT0AAAAAACHFHR6I4UQZ2E7ED2B7YEWAZQOJ2AA)

---

## AOP

- Programmation orientée Aspect
  - Paradigme de programmation
  - On s'intéresse aux aspects transverses de l'application (transactions)
  - La gestion des aspects est centralisée
  - On court-circuite les traitements métier pour y insérer les traitements relatifs aux aspects

<!--Spring AOP : implémetnation partielle, gestion des transactions, complémentaire de l'inversion de contrôle, très utilisé en interne dans le framework API riche => bon candidat-->

---

## Schéma Spring AOP



---

## Implémentation dans Spring _avec la proxyfication_

- [cglib (code generation library)](https://github.com/cglib/cglib?tab=readme-ov-file#cglib-) repackagé par Spring

- [Proxy du JDK](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/Proxy.html)

---

## Exemple simple dans Spring

code

---

## AOP avec Les contrôleurs de _metadata api_

schéma

---

## Configuration

```java
public void onApplicationEvent(@NonNull ApplicationContextInitializedEvent event) {
    /* ... */
    new ClassPathScanningCandidateComponentProvider(false){...}.findCandidateComponents(interfaceControllerPackage);
	/* ... */
	controllerMetadatas.forEach( classMetadata -> {
	    /* ... */
	    genericApplicationContext.registerBeanDefinition(advisorName,
            getPointcutBeanDefinition(namesOfMethodsToIntercept));
		genericApplicationContext.registerBean(
            classMetadata.getClassName(), ControllerProxyFactoryBean.class, 
            controllerInterface, advisorName);
		/* ..*/
	});
	/* ... */
}
```

---

## Configuration

```java
public class ControllerProxyFactoryBean extends ProxyFactoryBean {

    public ControllerProxyFactoryBean(Class<?> controllerClass, String advisorName) {
        setSingleton(true);
        setInterfaces(controllerClass);
        setInterceptorNames(advisorName);
    }
}
``` 

--- 

## Implémentation contrôleur générique

```java
public Optional<?> process(@NonNull Method method, @NonNull Object[] arguments)
    throws ArgumentException{
                                                                                                         
    String methodName=method.getName();                                                                                       
    Optional<QueryTemplate> queryTemplate= queryTemplateSupplier.get(methodName);                        
    if (queryTemplate.isEmpty()) {                                                                       
        throw new UnsupportedOperationException(STR."Method \{methodName} not supported");               
    }                                                                                                    
    String query = queryTemplate.get().format(arguments);                                                
    return ofNullable(unmarshaller.unmarshal(queryExecutor.execute(query), method));                     
}                                                                                                        
```

--- 

## Implémentation contrôleur générique


```java
public Optional<QueryTemplate> get(@NonNull String methodName) {              
    return Optional.ofNullable(switch (methodName) {                         
        case "getcogcom" -> queryTemplate().withFindByCode()                 
                .codeAtPosition(0).dateAtPosition(1)                                           
                .forTerritory("Commune");                                    
        case "getcogdep" -> queryTemplate().withFindByCode()                 
                .codeAtPosition(0).dateAtPosition(1)                                           
                .forTerritory("Departement");                                
        case "getTerritoireByTypeAndCode" -> queryTemplate().withFindByCode()
                .codeAtPosition(0).dateAtPosition(2)                                           
                .forUnknownTerritoryType(1);
		/* ... */
		default -> null;
	});
}
```

---

## Ca fonctionne !

---

## Conclusion

- Pistes d'amélioration pour le POC
  - simplifier la déclaration des _pointcuts_
  - Utiliser la réflexion pour associer _endpoints_ et requêtes SPARQL
  - Etre indépendant de Spring Boot
- Spring AOP : 
  - Outil puissant pour gérer des aspects transverses ou génériques dans une application Spring
  - Très bien intégré avec l'écosystème Spring
  - Plutôt pour les implémentations internes de Spring mais bon à connaître

---

## Lien

QR code

MERCI !

