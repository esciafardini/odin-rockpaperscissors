(ns rps.js-helpers)

(def console-log (.-log js/console))

(defn get-element-by-id [id]
  (.getElementById js/document id))

(defn set-text-content! [div-id text-content]
  (let [div (get-element-by-id div-id)]
    (set! (.. div -textContent) text-content)))
