(ns nsorg.reconcile
  (:require
    [nsorg.zip :as nzip]
    [rewrite-clj.zip :as zip]
    [rewrite-clj.parser :as parser]
    [rewrite-clj.node :as node]))




(def s "
(ns moi
  (:require
    [koira]
    [kissa]
    [elefantti]))")

;;; Representation as data structure
{:clauses [:type :require
           :libspecs [{:type      :libspec
                       :namespace 'kissa.a
                       :options   [{:key :refer
                                    :val '[foo bar]}]}
                      {:type      :prefix-libspec
                       :prefix    'asda
                       :libspecs [{:type :libspec
                                   :namespace 'c
                                   :options {:key :refer
                                             :val :all}}]}]]}



(def z (zip/of-string s))

(def n (parser/parse-string-all s))


(defn ns-clause? [form]
  (and (sequential? form)
       (keyword? (first form))))

(defn parse-libspec [zloc]
  {:type      :libspec
   :namespace (zip/sexpr (zip/down zloc))})

(defn parse-ns-clause [zloc]
  (let [sexpr (nzip/sexpr zloc)]
    (with-meta {:type     (first sexpr)
                :libspecs (vec (map-indexed (fn [i zloc]
                                              (with-meta
                                                (parse-libspec zloc)
                                                {::index i}))
                                            (nzip/right-nodes (zip/right (zip/down zloc)))))}
               {::node (zip/node zloc)})))

(defn parse-ns-clauses [zloc]
  (vec (filter identity (map-indexed (fn [i zloc]
                                       (when (ns-clause? (nzip/sexpr zloc))
                                         (with-meta (parse-ns-clause zloc)
                                                    {::index i})))
                                     (nzip/right-nodes (zip/down zloc))))))

(defn parse-ns-form [zloc]
  (let [sexpr (nzip/sexpr zloc)]
    (and (list? sexpr)
         (= (first sexpr) 'ns)
         (with-meta {:clauses (parse-ns-clauses zloc)}
                    {::node (zip/node zloc)}))))

(defn node [ns-form]
  (::node (meta ns-form)))



;(defn apply-changes-ns-clauses [zipper original modified]
;  )
;
;
;(defn apply-changes [zipper original modified]
;  (apply-changes-ns-clauses zipper (:clauses original) (:clauses modified)))
;
;(defprotocol NsFormReconciler
;  (editable [_])
;  (reconcile! [_ modified]))
;
;(defn create-reconciler [s]
;  (let [zipper (zip/of-string s)
;        editable (parse-ns-form zipper)]
;    (reify NsFormReconciler
;      (editable [_]
;        editable)
;      (reconcile! [_ modified]
;        (zip/string (apply-changes zipper editable modified))))))
;
;
;
;(def reconciler (create-reconciler s))




















