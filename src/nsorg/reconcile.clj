(ns nsorg.reconcile
  (:require
    [nsorg.zip :as nzip]
    [rewrite-clj.zip :as zip]
    [rewrite-clj.parser :as parser]
    [rewrite-clj.node :as node]))




(def s "
(ns moi
  (:require
    [koira] ;; do not touch!
    [kissa]
    [elefantti]))")

;;; Representation as data structure
{:clauses [:type :require
           :libspecs [{:type      :libspec
                       :namespace 'kissa.a
                       :options   [{:key :refer
                                    :val '[foo bar]}]}
                      {:type     :prefix-libspec
                       :prefix   'asda
                       :libspecs [{:type      :libspec
                                   :namespace 'c
                                   :options   {:key :refer
                                               :val :all}}]}]]}



(def z (zip/of-string s))

(def n (parser/parse-string-all s))


;;;; parsing part

(defn with-meta+ [obj m]
  (with-meta obj
             (merge (meta obj) m)))

(defn ns-clause? [form]
  (and (sequential? form)
       (keyword? (first form))))


(defn parse-sequential [zloc pred]
  (loop [zloc (zip/down zloc)
         done? false
         i 0
         children []]
    (cond
      done?
      {:node     (zip/node (zip/up zloc))
       :children children}

      (pred (nzip/sexpr zloc))
      (let [modified-zloc (zip/replace zloc [::placeholder i])]
        (if-let [new-zloc (zip/right modified-zloc)]
          (recur new-zloc false (inc i) (conj children (zip/node zloc)))
          (recur modified-zloc true (inc i) (conj children (zip/node zloc)))))

      :else
      (if-let [new-zloc (zip/right zloc)]
        (recur new-zloc false i children)
        (recur zloc true i children)))))


(defn parse-libspec [zloc]
  {:type      :libspec
   :namespace (zip/sexpr (zip/down zloc))})


(defn parse-ns-clause [zloc]
  (let [{:keys [node children]} (parse-sequential zloc (complement keyword?))]
    (with-meta+ {:type (first (nzip/sexpr zloc))
                 :libspecs (vec (map-indexed (fn [i child]
                                               (with-meta+ (parse-libspec (zip/edn child))
                                                           {::index i}))
                                             children))}
                {::node node})))

(defn parse-ns-clauses [zloc]
  (let [{:keys [node children]} (parse-sequential zloc ns-clause?)]
    (with-meta+ {:clauses (vec (map-indexed (fn [i child]
                                              (with-meta+ (parse-ns-clause (zip/edn child))
                                                          {::index i}))
                                            children))}
                {::node node})))

(defn parse-ns-form [zloc]
  (let [sexpr (nzip/sexpr zloc)]
    (and (list? sexpr)
         (= (first sexpr) 'ns)
         (parse-ns-clauses zloc))))

;;;;;;;;;


;;;;;; stringing part



(defn placeholder-node? [form]
  (and (vector? form)
       (= (first form) ::placeholder)))

(defn node-libspec [{:keys [namespace]}]
  (node/coerce namespace))

(defn node-ns-clause [{:keys [libspecs] :as clause}]
  (let [remaining-indices (set (map (comp ::index meta) libspecs))]
    (loop [zloc (zip/down (zip/edn (::node (meta clause))))
           done? false
           libspecs libspecs]
      (cond
        done?
        (zip/node (zip/up zloc))

        (placeholder-node? (nzip/sexpr zloc))
        (let [index (second (nzip/sexpr zloc))]
          (if (remaining-indices index)
            ;; replaced
            (let [modified-zloc (zip/replace zloc (node-libspec (first libspecs)))]
              (if-let [new-zloc (zip/right modified-zloc)]
                (recur new-zloc false (rest libspecs))
                (recur modified-zloc true (rest libspecs))))
            ;; deleted -- doesn't handle case of only 1 node
            (let [modified-zloc (zip/remove zloc)]
              (if-let [new-zloc (if (zip/leftmost? zloc)
                                  (zip/down modified-zloc)
                                  (zip/right modified-zloc))]
                (recur new-zloc false libspecs)
                (recur modified-zloc true libspecs)))))

        :else
        (if-let [new-zloc (zip/right zloc)]
          (recur new-zloc false libspecs)
          (recur zloc true libspecs))))))

(defn node-ns-form [{:keys [clauses] :as ns-form}]
  (let [remaining-indices (set (map (comp ::index meta) clauses))]
    (loop [zloc (zip/down (zip/edn (::node (meta ns-form))))
           done? false
           clauses clauses]
      (cond
        done?
        (zip/node (zip/up zloc))

        (placeholder-node? (nzip/sexpr zloc))
        (let [index (second (nzip/sexpr zloc))]
          (if (remaining-indices index)
            ;; replaced
            (let [modified-zloc (zip/replace zloc (node-ns-clause (first clauses)))]
              (if-let [new-zloc (zip/right modified-zloc)]
                (recur new-zloc false (rest clauses))
                (recur modified-zloc true (rest clauses))))
            ;; deleted -- doesn't handle case of only 1 node
            (let [modified-zloc (zip/remove zloc)]
              (if-let [new-zloc (if (zip/leftmost? zloc)
                                   (zip/down modified-zloc)
                                   (zip/right modified-zloc))]
                (recur new-zloc false clauses)
                (recur modified-zloc true clauses)))))

        :else
        (if-let [new-zloc (zip/right zloc)]
          (recur new-zloc false clauses)
          (recur zloc true clauses))))))



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




















