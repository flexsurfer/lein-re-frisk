# lein-re-frisk

Leiningen plugin that starts a web server for the remote debugging re-frame applications (react native, electron, web) using [re-frisk-remote](https://github.com/flexsurfer/re-frisk-remote) library. 

[<img src="2016-01-01-starting-clojure-today.jpg" width="100">](https://github.com/flexsurfer/re-frisk)

## Usage

[![Clojars](https://img.shields.io/clojars/v/lein-re-frisk.svg)](https://clojars.org/lein-re-frisk)


Add `[lein-re-frisk "0.5.4"]` into your global Leiningen config (`~/.lein/profiles.clj`) like so:

```cljs
{:user {:plugins [[lein-re-frisk "0.5.4"]]}}
```

or into the :plugins vector of your project.clj

```cljs
(defproject your-project "0.1.1"
  {:plugins [[lein-re-frisk "0.5.4"]]})
```

Start a web server in the current directory on the default port (4567):

    $ lein re-frisk

Or select a different port by supplying the port number on the command line:

    $ lein re-frisk 8095

Run re-frame application with the [re-frisk-remote](https://github.com/flexsurfer/re-frisk-remote) library. Enjoy!
