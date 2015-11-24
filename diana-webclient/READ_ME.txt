1.First setup :
	- Checkout to 1.3-web
	- (Install NodeJS if needed)
	- Move to diana-webclient root folder and run :
		npm install
	- Build the JS file with following command :
		gulp
	
	output will show if the build was succesful, if not :
	- for any package that might be missing
		npm install package-name

Test the app :
	- In project diana-webserver in eclipse run org.openflexo.server.Server
	- visit http://localhost:8080/

When editing the TypeScript files :
	- If any TS files were created, add their references to the compiler, with command :
		gulp gen-ts-refs
	- In any case, build the JS files with command :
		gulp
