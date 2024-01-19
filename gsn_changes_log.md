# Changes log 

# First running version as of June 13:

## Deployment 
- On any computer that can run VirtualBox (or any other supported virtual machine provider), install Vagrant

## Vagrantfile: 
- choose a newer box.. in our case debian/bullseye64
- delete openjdk-7-jre as we will use a previously downloaded jdk(jdk-7u80-linux-x64) as the openjdk-7 and 8 ar no longer available
- delete all ```wget``` and ````dpkg```` calls as they will only use the previously compiled package versions.
- add  ```git curl nodejs npm```
- start with ```vagrant up```
- connect with ```vagrant ssh```

A predefined working Vagrant file can be seen below:
```
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure(2) do |config|
  # The most common configuration options are documented and commented below.
  # For a complete reference, please see the online documentation at
  # https://docs.vagrantup.com.

  # Every Vagrant development environment requires a box. You can search for
  # boxes at https://atlas.hashicorp.com/search.
  config.vm.box = "debian/bullseye64"

  # Disable automatic box update checking. If you disable this, then
  # boxes will only be checked for updates when the user runs
  # `vagrant box outdated`. This is not recommended.
  # config.vm.box_check_update = false

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine. In the example below,
  # accessing "localhost:8080" will access port 80 on the guest machine.
  config.vm.network "forwarded_port", guest: 9000, host: 9000
  config.vm.network "forwarded_port", guest: 80, host: 8000

  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  # config.vm.network "private_network", ip: "192.168.33.10"

  # Create a public network, which generally matched to bridged network.
  # Bridged networks make the machine appear as another physical device on
  # your network.
  # config.vm.network "public_network"

  # Share an additional folder to the guest VM. The first argument is
  # the path on the host to the actual folder. The second argument is
  # the path on the guest to mount the folder. And the optional third
  # argument is a set of non-required options.
  # config.vm.synced_folder "../data", "/vagrant_data"

  # Provider-specific configuration so you can fine-tune various
  # backing providers for Vagrant. These expose provider-specific options.
  # Example for VirtualBox:
  #
  # config.vm.provider "virtualbox" do |vb|
  #   # Display the VirtualBox GUI when booting the machine
  #   vb.gui = true
  #
  #   # Customize the amount of memory on the VM:
  #   vb.memory = "1024"
  # end
  #
  # View the documentation for the provider you are using for more
  # information on available options.

  # Define a Vagrant Push strategy for pushing to Atlas. Other push strategies
  # such as FTP and Heroku are also available. See the documentation at
  # https://docs.vagrantup.com/v2/push/atlas.html for more information.
  # config.push.define "atlas" do |push|
  #   push.app = "YOUR_ATLAS_USERNAME/YOUR_APPLICATION_NAME"
  # end

  # Enable provisioning with a shell script. Additional provisioners such as
  # Puppet, Chef, Ansible, Salt, and Docker are also available. Please see the
  # documentation for more information about their specific syntax and use.

  config.vm.provision "shell", inline: <<-SHELL
    sudo apt-get update
    sudo apt-get install -y python3 python3-pip python3-virtualenv virtualenv nginx wget git curl nodejs npm
    sudo npm install -g bower
  SHELL
  
end
```


## On Virtual machine:
### install sbt: 

```sudo apt-get update
sudo apt-get install apt-transport-https curl gnupg -yqq
echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo -H gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
sudo chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
sudo apt-get update
sudo apt-get install sbt
```

### install java:
- get java: ```wget https://files-cdn.liferay.com/mirrors/download.oracle.com/otn-pub/java/jdk/8u121-b13/jdk-8u121-linux-x64.tar.gz```

- install: 
```
sudo tar -xzvf jdk-8u121-linux-x64.tar.gz

sudo mkdir -p /usr/lib/jvm
sudo mv ./jdk1.8.0_121/ /usr/lib/jvm/

sudo update-alternatives --install "/usr/bin/java" "java" "/usr/lib/jvm/jdk1.8.0_121/bin/java" 1
sudo update-alternatives --install "/usr/bin/javac" "javac" "/usr/lib/jvm/jdk1.8.0_121/bin/javac" 1
sudo update-alternatives --install "/usr/bin/javaws" "javaws" "/usr/lib/jvm/jdk1.8.0_121/bin/javaws" 1

sudo chmod a+x /usr/bin/java 
sudo chmod a+x /usr/bin/javac
sudo chmod a+x /usr/bin/javaws

sudo chown -R root:root /usr/lib/jvm/jdk1.8.0_121/

sudo update-alternatives --config java
sudo update-alternatives --config javac
sudo update-alternatives --config javaws
```

-```java -version``` should then print: 
```java version "1.8.0_121"
Java(TM) SE Runtime Environment (build 1.8.0_121-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.121-b13, mixed mode)
```

### Clone repository: 

```
git clone https://github.com/LSIR/gsn.git
```


## Repository: 

### main build.sbt: 
line 6:
```
javacOptions in (Compile, compile) ++= Seq("-source", "1.7", "-target", "1.7"),
```

lines 9-10: 
```
    "Typesafe Repository" at "https://repo.maven.apache.org/maven2/",
    "osgeo" at "https://repo.osgeo.org/repository/release/",
```

### project/plugins.sbt:
top needs to be changed to: 
```
// The Typesafe repository 
resolvers += "Typesafe repository" at "https://repo.maven.apache.org/maven2/"
resolvers += Resolver.typesafeRepo("releases")
```

### gsn-tools:
#### build.sbt:
line 12: change ```javax.media``` from ```  "javax.media" % "jai_core" % "1.1.3" from "http://download.osgeo.org/webdav/geotools/javax/media/jai_core/1.1.3/jai_core-1.1.3.jar",```  to  ```"javax.media" % "jai_core" % "1.1.3",```

### gsn-services:
#### conf/application.conf: 
around line 15: change ```gsn.location="/usr/share/gsn-core/``` to ```gsn.location="/home/vagrant/gsn/"```

around line 17: change ```gsn.vslocation=${gsn.location}/conf/virtual-sensors``` to ```gsn.vslocation=${gsn.location}/virtual-sensors```

### gsn-webui: 

**requirements.txt**: 
```
Django
django-bower
requests
requests-cache
jsonfield
django-all-access
gunicorn
```

**settings.py:**
``` """
Django settings for mysite project.

Generated by 'django-admin startproject' using Django 1.8.3.

For more information on this file, see
https://docs.djangoproject.com/en/1.8/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/1.8/ref/settings/
"""

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
import os

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/1.8/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = '4z_g+i&5omq3lbl@%*t!r1(6ag)9o619n2w@!eu0y@lg=p2gmj'

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

ALLOWED_HOSTS = []

# Application definition

AUTH_USER_MODEL = "gsn.GSNUser"

INSTALLED_APPS = (
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'djangobower',
    'gsn',
    'allaccess',
    )

MIDDLEWARE = (
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    #'django.contrib.auth.middleware.SessionAuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
    'django.middleware.security.SecurityMiddleware',
    )

ROOT_URLCONF = 'app.urls'

TEMPLATES = [{
    'BACKEND': 'django.template.backends.django.DjangoTemplates',
    'DIRS': [os.path.join(BASE_DIR, 'templates')],
    'APP_DIRS': True,
    'OPTIONS': {
        'context_processors': ['django.template.context_processors.debug',
                               'django.template.context_processors.request',
                               'django.contrib.auth.context_processors.auth',
                               'django.contrib.messages.context_processors.messages',
                               ],
        },
    }, ]

AUTHENTICATION_BACKENDS = (  # Default backend
    'django.contrib.auth.backends.ModelBackend',  # Additional backend
    'allaccess.backends.AuthorizedServiceBackend',
    )

WSGI_APPLICATION = 'app.wsgi.application'

# Database
# https://docs.djangoproject.com/en/1.8/ref/settings/#databases

BOWER_COMPONENTS_ROOT = os.path.join(BASE_DIR, 'components')

BOWER_INSTALLED_APPS = (
    "angularjs",
    "angular-route",
    "angular-bootstrap-datetimepicker",
    "angular-date-time-input",
    "dirPagination",
    "angular-bootstrap",
    "bootstrap",
    "font-awesome",
    "metisMenu#",
    "jquery",
    "jquery-ui",
    "angular-tabs",
    "angular-local-storage",
    "ngmap",
    "markerclustererplus",
    "angular-chart.js",
    "highcharts",
    "highcharts-ng",
    "ngAutocomplete",
    "angular-spinner",
    "moment",
    "angular-websocket",
)

# Internationalization
# https://docs.djangoproject.com/en/1.8/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'CET'

USE_I18N = True

USE_L10N = True

USE_TZ = True

# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.8/howto/static-files/

STATIC_URL = '/static/'
STATIC_ROOT = './static/'

STATICFILES_DIRS = (
    os.path.join(BASE_DIR, "static-files"),
    os.path.join(BASE_DIR, "node_modules"),
)

STATICFILES_FINDERS = (
    'django.contrib.staticfiles.finders.FileSystemFinder',
    'django.contrib.staticfiles.finders.AppDirectoriesFinder',
    'djangobower.finders.BowerFinder',
)

# Custom setting

LOGIN_URL = '/login/'


try:
    from app.settingsLocal import *
except ImportError:
    raise 

```

### npm management

init
``` npm init ```

copy to created package.json: 
```
{
  "name": "gsn-webui",
  "version": "1.0.0",
  "description": "This is a web interface for exploring the data processed by GSN. It is based on the python Django framework and AngulaJS. It interacts with the Services module.",
  "main": "index.html",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "author": "",
  "license": "ISC",
  "dependencies": {
    "angular": "^1.8.2",
    "angular-bootstrap-datetimepicker": "^1.0.1",
    "angular-chart.js": "^1.1.1",
    "angular-date-time-input": "^1.2.1",
    "angular-route": "^1.5.11",
    "bootstrap": "^3.3.7",
    "font-awesome": "^4.7.0",
    "highcharts": "^8.2.2",
    "jquery": "^3.6.0",
    "jquery-ui": "^1.12.1",
    "markerclustererplus": "^2.1.4",
    "metismenu": "^2.7.9",
    "moment": "^2.29.1"
  }
}
```
npm install: 
```npm install```

npm install command for other modules: 
```
npm install angular-utils-pagination angular-bootstrap angular-tabs angular-local-storage ngmap highcharts-ng ng-autocomplete angular-spinner moment angular-websocket jquery-ui-dist
```



#### starting sbt
in /gsn/
```sbt```

Then you can run the following tasks in sbt:

* clean: remove generated files
* compile: compiles the modules
* package: build jar packages
* project [core|extra|tools|services|webui]: select a specific projet

In the project core you can use ``re-start`` to launch gsn-core for development.

In the project services you can use ``run`` to start the web api in development mode.



### login credentials for http://localhost:9000/ws/login
username: root@localhost
pw: changeme


#### in the management board at clients: 
edit client: such that client id and client secret are the same as in app/settingsLocal.py and link user
```
GSN = {
    'CLIENT_ID': 'web-gui-public',
    'CLIENT_SECRET': 'web-gui-public',
    'SERVICE_URL_PUBLIC': 'http://localhost:9000/ws/', # used for in-browser redirects
    'SERVICE_URL_LOCAL': 'http://localhost:9000/ws/',  # used for on-server direct calls
    'WEBUI_URL': 'http://localhost:8000/',             # used for in-browser redirects
    'MAX_QUERY_SIZE': 5000,
}



# New GSN-WEBUI-FRONTEND: 

install sudo npm install -g @angular/cli
```


### GSN configuration server
#### switchable java bellsoft:
to be able to switch between java version with the command
```
switch_java 8
```
#### Add BellSoft official GPG key and setup the repository

```
wget -q -O - https://download.bell-sw.com/pki/GPG-KEY-bellsoft | sudo apt-key add -
```

```
echo "deb [arch=amd64] https://apt.bell-sw.com/ stable main" | sudo tee /etc/apt/sources.list.d/bellsoft.list
```

```
sudo apt-get update
sudo apt-get install bellsoft-java8
```

open ~/.bashrc:

```
nano ~/.bashrc
```
and insert: (replace /path/to/java8/bin/java with according path)
```
function switch_java() {
    if [ "$1" = "8" ]; then
        sudo update-alternatives --set java /path/to/java8/bin/java
    elif [ "$1" = "17" ]; then
        sudo update-alternatives --set java /path/to/java17/bin/java
    else
        echo "Invalid Java version. Usage: switch_java 8|17"
    fi
}
```

reload

```
source ~/.bashrc
```


### Add release packages to server:
depakage using:
```
sudo dpkg -i /path/to/deb/file
```

start the core or service using: 
```
sudo systemctl start gsn-core.service
sudo systemctl start gsn-services.service
```

don't forget to add the according virtual sensors in (if no folder exists,create a new folder virtual-sensors):
```
/usr/share/gsn-core/virtual-sensors
```

### Database configuration:
config file for database can be found at:
```
/usr/share/gsn-core/conf/gsn.xml
```