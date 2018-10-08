(ns nsorg.ns-analyzer
  (:require [clojure.tools.analyzer.ast :as ast]
            [clojure.tools.analyzer.jvm :as analyzer])
  (:import (java.io PushbackReader InputStreamReader ByteArrayInputStream)))

(defn read-forms [s]
  (with-open [reader (PushbackReader. (InputStreamReader. (ByteArrayInputStream. (.getBytes s "UTF-8"))))]
    (doall
      (take-while #(not= % ::eof) (repeatedly #(read reader false ::eof))))))

(defn analyze [s]
  (binding [*ns* *ns*]
    {:symbols (into {}
                    (for [form (read-forms s)
                          {:keys [op var form]} (ast/nodes (analyzer/analyze+eval form))
                          :when (= op :var)]
                      {form var}))}))
