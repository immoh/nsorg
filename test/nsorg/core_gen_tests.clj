(ns nsorg.core-gen-tests
  (:require [clojure.core.specs.alpha :as core-specs]
            [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]
            [midje.experimental :refer [for-all]]
            [midje.sweet :refer :all]
            [nsorg.core :as nsorg]))

;; Patch spec for :gen-class method to add generator
(s/def ::core-specs/method (s/with-gen (s/and vector?
                                              (s/cat :name simple-symbol?
                                                     :param-types ::core-specs/signature
                                                     :return-type simple-symbol?))
                                       #(gen/fmap vec (s/gen (s/cat :name simple-symbol?
                                                                    :param-types ::core-specs/signature
                                                                    :return-type simple-symbol?)))))

(defn is-rewrite-of [original]
  (fn [actual]
    (let [original-freqs (frequencies original)]
      (every? (fn [[char count]]
                (<= count (get original-freqs char 0)))
              (frequencies actual)))))

(for-all
  [ns-form (gen/fmap
             (fn [ns-form-args]
               (str (cons 'ns ns-form-args)))
             (s/gen ::core-specs/ns-form))]
  (fact
    "Rewrite function is able to handle arbitary ns forms"
    (nsorg/rewrite-ns-form ns-form) => (is-rewrite-of ns-form)))
