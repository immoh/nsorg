(defproject nsorg "0.1.3"
  :description "Clojure library for organizing ns form"
  :url "https://github.com/immoh/nsorg"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[rewrite-clj "0.6.0"]]
  :profiles {:dev {:dependencies [[midje "1.9.2-alpha1"]
                                  [org.clojure/clojure "1.9.0"]]
                   :plugins      [[lein-codox "0.10.3"]
                                  [lein-midje "3.2.1"]]}}
  :codox {:source-uri "https://github.com/immoh/nsorg/blob/{version}/{filepath}#L{line}"})
