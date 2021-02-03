# M1 Secure Development : Mobile applications



### Réponse aux questions

- Explain how you ensure user is the right one starting the app
  - login + mdp hashé, enregistré dans db chiffrée
- How do you securely save user's data on your phone ?
  - db chiffrée, clé générée aléatoirement et localement, enregistrée dans fichier chiffré et mise en mémoire que le temps d'établir le lien avec la db (vidange mémoire)
- How did you hide the API url ?
  - Enigma ou voir comment mettre un fichier chiffré dans l'apk (idéalement, compatible avec fonctions de marc)
- Screenshots of your application 
  - voir fin de ce readme



### Fonctionnement et features

fontionnement général

features :

- ...
- ...
- ...



### Maquette et preview











Hey you all,  
In this repository you will find everything you need to do the workshop.

## Report guidelines

- You create a github/gitlab public project **only** containing:
  - Your source code in a **src** folder
  - <your_application>.apk
  - README.md
- You send your repository link **before 11pm59 on the 1th february 2021**. After this timeline, you will loose 1pts per hour.

## Exercice

Using Android Studio (or any editor of your choice), you will have to create a mobile application.  
You can choose the language you want between Kotlin and Java.  

The goal is to create a secure application to see your bank accounts.   

### Requirements
- This application must be available offline.
- A refresh button allows the user to update its accounts.
- Only the phone's user can start the app
- Exchanges with API must be secure ( with TLS)


### API
https://6007f1a4309f8b0017ee5022.mockapi.io/api/m1/:endpoint


```json
/config/1
You only have "read" rights on the config enpoint. 

  {
    "id": "1",
    "name": "Lesley",
    "lastname": "Kuhic"
  }
```


```json
/accounts
You can read and create new accounts. You cannot modify, nor delete.

  {
    "id": "2",
    "account_name": "Home Loan Account",
    "amount": "199.04",
    "iban": "XK753002606900617470",
    "currency": "RD$"
  }
```


### README.md content

- Explain how you ensure user is the right one starting the app
- How do you securely save user's data on your phone ?
- How did you hide the API url ?
- Screenshots of your application 

### Report scoring

- Your README file contains answers to the asked questions (2pts)

Your APK will be audited in the same way as a classic mobile pentest 
- You start with 10pts and you will loose points if:
    - Your application doesn't respect requirements (-10pts)
    - Api url is recoverable (-2pts)
    - Your application can be accessed by any user (-2pts)
    - Stored data can be recovered (-2pts)
    - Permissions are too wide (-2pts)
- The originality of your solution is scored on 3
- The complexity of your solution is scored on 5
- UX/UI will not be scored



