(defproject boss "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.github.seancorfield/next.jdbc "1.3.847"]
                 [org.xerial/sqlite-jdbc "3.36.0.3"]
                 [com.github.seancorfield/honeysql "2.4.972"]]
  :repl-options {:init-ns boss.core})
