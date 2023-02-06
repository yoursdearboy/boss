(defproject boss "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/clojurescript "1.10.597"]
                 [environ "1.2.0"]
                 [com.github.seancorfield/next.jdbc "1.3.847"]
                 [org.xerial/sqlite-jdbc "3.36.0.3"]
                 [com.github.seancorfield/honeysql "2.4.972"]
                 [ring "1.9.6"]
                 [compojure "1.7.0"]
                 [reagent "0.10.0"]]
  :plugins [[lein-environ "1.2.0"]
            [lein-cljsbuild "1.1.7"]]
  :profiles {:dev  {:env {:port "3000" :database-url "jdbc:sqlite:///test/demo.db"}}
             :test {:env {:port "3000" :database-url "jdbc:sqlite:///test/demo.db"}}}
  :cljsbuild {:builds [{:source-paths ["src/boss/frontend"] 
                        :compiler {:optimizations :simple
                                   :output-to "resources/public/main.js"}}]}
  :repl-options {:init-ns boss.core})
