# Bachelor-thesis-2023_Cryogenetics
Bachelor thesis directory for the Cryogenetics task. 
## Authors
[Axel Jacobsen](https://github.com/AxelJacobsen) <br>
[Håvard Bø](https://github.com/Haavbo) <br>
[Lars Ruud](https://github.com/Thefantasticbagle) <br>
[Matthias Greeven](https://github.com/TheGrevling)

## Directory Navigation
#### Code
All code in the directory is located in the **/Code/** folder. <br>
**/Backend/** contains the backend golang server. <br>
**/Frontend/Mobile application/** contains the android studio project code. <br>
**/Frontend/admin-website/** contains the react website code with running instructions.
#### Documentation
The documentation folder contains all the related documents for the project. This includes:
* Our thesis
* Conceptual models
* Endpoint usage instructions
* Use case diagram
#### Images
The images folder contains all icons used in our applications. <br>
These icons are all self created and therefore have no copyright associated to them. <br>
If you want the source file for editing the icons contact [Axel Jacobsen](https://github.com/AxelJacobsen). <br>
#### Product
The product folder only contains the current, as well as past, databases.

## Directory Usage
#### Database Instructions
To run the project locally, you first need to install the SQL database into PhP MyAdmin. <br>
The database file is located in: **Product/Database/cryogenetics_database.sql**
Once the database is loaded use XAMPP to run Apache and MySQL. With this the backend can now access the database. <br>
#### Backend Instructions
Next, Launch the backend server by navigating to **Code/Backend** <br>
From here you run the backend with the command: **go run \cmd\server.go**.<br> 
*You know it works when you get a "Listening on port XXXX" in the console.* <br>
#### Web Instructions
See [**/Code/Frontend/admin-website/**](Code/Frontend/admin-website/README.md) for running instructions.
#### Mobile Instructions
See [**/Code/Frontend/Mobile&nbsp;application/**](Code/Frontend/Mobile&nbsp;application/ReadMe.md) for running instructions.
