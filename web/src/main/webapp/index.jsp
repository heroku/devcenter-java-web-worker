<!doctype html>

<html>
  <head>
    <meta charset="utf-8">
    <title>Java Web-Worker Template</title>

    <meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="/resources/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link href="/resources/css/bootstrap-responsive.min.css" rel="stylesheet" type="text/css">
    <link href="" rel="stylesheet" type="text/css">

    <!--
    IMPORTANT:
    This is Heroku specific styling. Remove to customize.
    -->
    <link href="/resources/css/heroku.css" rel="stylesheet">
    <style type="text/css">
      .instructions { display: none; }
      .instructions li { margin-bottom: 10px; }
      .instructions h2 { margin: 18px 0;}
      .instructions blockquote { margin-top: 10px; }
      .screenshot {
        margin-top: 10px;
        display:block;
      }
      .screenshot a {
        padding: 0;
        line-height: 1;
        display: inline-block;
        text-decoration: none;
      }
      .screenshot img, .tool-choice img {
        border: 1px solid #ddd;
        -webkit-border-radius: 4px;
        -moz-border-radius: 4px;
        border-radius: 4px;
        -webkit-box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075);
        -moz-box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075);
        box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075);
      }
    </style>
    <!-- /// -->
    <script type="text/javascript">
      <!--
      function appname() {
          return location.hostname.substring(0,location.hostname.indexOf("."));
      }
      // -->
    </script>
  </head>

  <body>
    <div class="navbar navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a href="/" class="brand">Java Web-Worker Template</a>
          <!--
          IMPORTANT:
          This is Heroku specific markup. Remove to customize.
          -->
          <a href="/" class="brand" id="heroku">by <strong>heroku</strong></a>
          <!-- /// -->
        </div>
      </div>
    </div>

    <div class="container" id="getting-started">
      <div class="row">
        <div class="span8 offset2">
          <h1 class="alert alert-success">Your app is ready!</h1>
          
          <div class="page-header">
            <h1>Get started with your Java Web-Worker Template</h1>
          </div>
          
          <div style="margin-bottom: 20px">
            This is a template for a Java web app that sends expensive operations to a message queue for asynchronous processing by one or more worker dynos.
            The sample code uses the <a href="http://www.springsource.org/spring-amqp">Spring AMPQ</a> library along with the
            Heroku <a href="https://addons.heroku.com/cloudamqp">RabbitMQ add-on</a>.
            To try it out go to the <a href="/spring/bigOp">Big Operation Submission Form</a>. Then use the Command Line to deploy some changes.
          </div>
          
        <ul id="tab" class="nav nav-tabs">
            <li class="active"><a href="#cli-instructions" data-toggle="tab">Use Command Line</a></li>
        </ul>

        <div class="tab-content">

          <div id="cli-instructions" class="instructions tab-pane active">
            <a name="using-cli"></a>
            <h1>Using Command Line:</h1>

            <h2>Step 1. Setup your environment</h2>
            <ol>
              <li>Install the <a href="http://toolbelt.heroku.com">Heroku Toolbelt</a>.</li>
              <li>Install <a href="http://maven.apache.org/download.html">Maven</a>.</li>
            </ol>

            <h2>Step 2. Login to Heroku</h2>
            <code>heroku login</code>
            <blockquote>
              Be sure to create, or associate an SSH key with your account.
            </blockquote>
            <pre>
$ heroku login
Enter your Heroku credentials.
Email: naaman@heroku.com
Password:
Could not find an existing public key.
Would you like to generate one? [Yn] Y
Generating new SSH public key.
Uploading SSH public key /Users/Administrator/.ssh/id_rsa.pub
Authentication successful.</pre>

            <h2>Step 3. Clone the App</h2>
            <code>git clone -o heroku git@heroku.com:<script>document.write(appname())</script>.git</code>

            <h2>Step 4. Makes some changes to the app</h2>
            <ol>
                <li>Open <code>worker/src/main/java/com/heroku/devcenter/BigOperationWorker.java</code> in your favorite editor</li>
                <li>Change the listener to print the name of the operation in uppercase by adding to the <code>onMessage()</code> method:<br/>
                    <code>System.out.println(bigOp.getName().toUpperCase());</code></li>
            </ol>

            <h2>Step 5. Make sure the app still compiles</h2>
            <code>mvn clean package</code>

            <h2>Step 6. Deploy your changes</h2>
            <ol>
              <li><code>git commit -am "New changes to deploy"</code></li>
              <li><code>git push heroku master</code></li>
            </ol>

            <div class="hero-unit">
              <h1>Done!</h1>
              <p>You've just cloned, modified, and deployed a brand new app.</p>
              <a href="/spring/bigOp" class="btn btn-primary btn-large">See your changes</a>
                
              <p style="margin-top: 20px">Learn more at the   
              <a href="http://devcenter.heroku.com/categories/java">Heroku Dev Center</a></p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
  <!-- end tab content -->  
  </div>

    <script src="/resources/js/jquery-1.7.1.min.js"></script>
    <script src="/resources/js/bootstrap-modal.js"></script>
    <script src="/resources/js/bootstrap-tab.js"></script>
  </body>
</html>
