(ns malli.dev.cljs
  #?(:cljs (:require-macros [malli.dev.cljs]))
  #?(:cljs (:require [malli.instrument.cljs]
                     [malli.dev.pretty :as pretty]))
  #?(:clj (:require [malli.clj-kondo :as clj-kondo]
                    [malli.dev.pretty :as pretty]
                    [malli.instrument.cljs :as mi]
                    [malli.core :as m])))

#?(:clj (defmacro stop!
          "Stops instrumentation for all functions vars and removes clj-kondo type annotations."
          []
          `(do
             ~(mi/-unstrument &env nil)
             ~(do (clj-kondo/save! {}) nil))))

#?(:clj
   (defn start!* [env options]
     `(do
        (js/console.groupCollapsed "Instrumentation done")
        ;; register all function schemas and instrument them based on the options
        ;; first clear out all metadata schemas to support dev-time removal of metadata schemas on functions - they should not be instrumented
        ~(m/-deregister-metadata-function-schemas! :cljs)
        ~(mi/-collect-all-ns env)
        ~(mi/-instrument env options)
        (js/console.groupEnd))))

#?(:clj (defmacro start!
          "Collects defn schemas from all loaded namespaces and starts instrumentation for
           a filtered set of function Vars (e.g. `defn`s). See [[malli.core/-instrument]] for possible options.
           Differences from Clojure `malli.dev/start!`:

           - Does not unstrument functions - this is handled by hot reloading.
           - Does not emit clj-kondo type annotations. See `malli.clj-kondo/print-cljs!` to print clj-kondo config.
           - Does not re-instrument functions if the function schemas change - use hot reloading to get a similar effect."
          ([] (start!* &env {:report `(pretty/thrower)}))
          ([options] (start!* &env options))))

#?(:clj (defmacro collect-all! [] (mi/-collect-all-ns &env)))


#?(:clj (defmacro deregister-function-schemas! []
          (m/-deregister-function-schemas! :cljs)
          nil))
