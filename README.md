# Facebook Example

A Facebook Messenger Bot Example in Clojure

### Facebook and app setup


1. Make sure you have followed the [Lemmings Clojure and Atom Editor setup](https://lemmings.io/clojure-and-atom-editor-setup-40f8f09237b4).

2. Start a new vagrant session by opening a new [terminal](http://blog.teamtreehouse.com/introduction-to-the-mac-os-x-command-line) window (on Windows you can use [Git BASH](https://git-for-windows.github.io/)), navigate to the 'clojure-master' directory you downloaded [before](https://github.com/lemmings-io/clojure) which contains the `Vagrantfile` and run `vagrant ssh`.

3. In the VM, change into the projects directory

   ```shell
   cd projects/
   ```

4. "Clone" the Facebook example into your Vagrant environment

    ```shell
    git clone https://github.com/lemmings-io/02-facebook-example.git
    ```

    Now change into the Facebook example directory:

    ```shell
    cd 02-facebook-example
    ```

5. Start the nREPL server

    Start another VM session by opening a new terminal window (Cmd-T) and run `vagrant ssh`.

    Navigate to the project folder you just created with `cd projects/02-facebook-example`.

    Start the nREPL server with `lein repl :headless :host 0.0.0.0 :port 7888` as described in the [Clojure and Atom Editor Setup](https://lemmings.io/clojure-and-atom-editor-setup-40f8f09237b4)

6. Open your project in Atom.

   Start Atom and open the folder `clojure-master/projects/02-facebook-example`
   as an Atom project.

   There's two things to know about Atom:

   1. Ctrl-P on Windows and Cmd-P on macOS opens a file search. You can use
   this to jump to any file in your project quickly.

   2. Ctrl-Shift-P on Windows and Cmd-Shift-P on macOS opens the
   Atom Command Palette. You use it to start the nREPL or enable Autoeval.

7. Setup a Facebook Page, Facebook app, create a Page Access Token and link the app to the page by following this [step-by-step guide](https://github.com/prometheus-ai/fb-messenger-clj/wiki/Facebook-Setup).


4. Provide your Facebook Page Access Token, Verify Token and Page Secret for local development by creating a file called `profiles.clj` in your working directory `<your-project-name>/profiles.clj`

		{:dev {:env {:page-access-token "REPLACE"
		   		   	  :verify-token "REPLACE"}}}

### Starting the development environment

1. Start a new VM shell session via `vagrant ssh` in your terminal.

2. Once logged in to the VM shell change into the facebook-example project directory

		cd 02-facebook-example/

3. Run `ngrok http 3000` ([read more about ngrok](https://ngrok.com))

	![ngrok Server](resources/doc/images/ngrok.png)

4. Start another new VM shell session via `vagrant ssh` in a new terminal window.

5. Start the local server

		lein ring server

	![Lein Server](resources/doc/images/lein-ring-server.png)

	Via the [lein-ring doc](https://github.com/weavejester/lein-ring): by default, this command attempts to find a free port, starting at 3000.

### Check if the app is running

1. Visit the https URL of the ngrok process you've started earlier in this guide.  
	E.g. `https://0db8caac.ngrok.io`
	
	If everything went right, you'll see "Hello Lemming :)" in your Browser 🎈
	
	![Hello Lemming](resources/doc/images/welcome-browser.png)

2. In your Facebook Developer App go to "Webhooks" in the left sidebar and add `Callback URL` and `Verify Token` accordingly:

	![Webhook Setup](resources/doc/images/webhook-setup.png)

3. Click "Verify and Save" and your app is connected to Facebook's Messenger API. 🎈

	![Webhook Success](resources/doc/images/webhook-success.png)

4. Then go to "Messenger" in the left sidebar and in the section "Webhooks" select your page to subscribe your webhook to the pages events. Subscribe to the events `messages` and `messaging_postbacks`, you can subscribe to more events later.

	![Webhook Subscription](resources/doc/images/webhook-subscription.png)
	![Webhook Events](resources/doc/images/webhook-events.png)

5. Go to your Facebook Page and send a message and your bot echo's your input. Congratulations!💧

	![Echo Bot](resources/doc/images/echo-bot.png)
	
Check out the code to find out more. Also try sending "image", "help" or thumbs up to your bot. 🙂

### Deploying to Heroku

1. If you haven't yet, create an account at [Heroku](https://signup.heroku.com/dc).

2. In the VM shell (ssh) session make sure you are in the right directory

  ```shell
  cd projects/02-facebook-example
  ```

2. In the VM shell session login to your Heroku account via `heroku login`.

	```
	-> heroku login
	Enter your Heroku credentials.
	Email:
	Password:
	```

3. Create an app on Heroku for your bot. Type `heroku create` into your command prompt at the root of the project.

	```
	-> heroku create
	Creating gentle-plateau-38046... done, stack is cedar-14
	https://gentle-plateau-38046.herokuapp.com/ | https://git.heroku.com/gentle-plateau-38046.git
	Git remote heroku added
	```

4. Do a command line `heroku config` for each one of your tokens:

	```
	heroku config:set PAGE_ACCESS_TOKEN=your_page_access_token
	heroku config:set VERIFY_TOKEN=your_verify_token
	```

5. You can deploy to heroku with the following command:

	```shell
	./heroku.sh
	```

	You should see it deploy. 🍵

8. Now we need to setup your app on Facebook with your app hosted on Heroku.

	As you've done it earlier for your local environment, in your Facebook Developer App click on the "Webhooks" section in the left sidebar and add `Callback URL` (e.g. `https://gentle-plateau-38046.herokuapp.com/webhook`) and `Verify Token` accordingly.
	
	![Heroku Webhook Setup](resources/doc/images/heroku-webhook-setup.png)

9. Click "Verify and Save" and your Heroku app is connected to Facebook's Messenger API 🎈

	![Heroku Webhook Success](resources/doc/images/heroku-webhook-success.png)
	
10. Go to your Facebook Page and send a message and your bot should respond to you. Kudos! 🙂💧

	![Echo Bot](resources/doc/images/echo-bot.png)

### Links
* [How to start up your development environment](https://github.com/lemmings-io/02-facebook-example/wiki/How-to-start-up-your-development-environment)
* [Getting started with GitHub and Git Version Control](https://guides.github.com/activities/hello-world/)
* [JSON Database Example](https://gist.github.com/sido378/0676b4ae6e264a73cc18737aa9496c69)
