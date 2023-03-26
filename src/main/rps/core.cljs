(ns rps.core
  (:require
   [clojure.set :as set]
   [clojure.string :as string]
   [cljs.core :as c]))

(def log (.-log js/console))

(def computer-choices
  {0 :rock
   1 :paper
   2 :scissors})

(def ui-messages
  {:rock "Rock beats scissors."
   :paper "Paper beats rock."
   :scissors "Scissors beats paper."
   :tie "Tie game."
   :error "Invalid user input."})

(defn get-winner
  "Returns keyword of :user or :computer based on winning-selection"
  [winning-selection game-data]
  (-> game-data
      set/map-invert
      (get winning-selection)))

(defn get-ui-message
  "Returns UI output for game"
  [selection]
  (get ui-messages selection))

(defn outcome [selection game-data]
  {:winner (get-winner selection game-data)
   :ui-message (get-ui-message selection)})

(defn determine-winner
  "If there is a winner, a map will be returned.
   Otherwise, a keyword that signifies bad user input (:error) or a tie game (:tie)"
  [data]
  (let [choices (set (vals data))]
    (cond
      (= 1 (count choices))
      {:winner :tie :ui-message (get-ui-message :tie)}

      (= choices #{:rock :paper})
      (outcome :paper data)

      (= choices #{:rock :scissors})
      (outcome :rock data)

      (= choices #{:scissors :paper})
      (outcome :scissors data))))

(defn get-computer-choice! []
  (let [n (rand-int 3)]
    (get computer-choices n)))

(defn play-round [user-selection]
  (let [computer-selection (get-computer-choice!)
        game-info {:user user-selection
                   :computer computer-selection}]
    (determine-winner game-info)))

(defn set-text-content! [div-id text-content]
  (let [div (.getElementById js/document div-id)]
    (set! (.. div -textContent) text-content)))

(defn update-text-content! [div-id f]
  (let [div (.getElementById js/document div-id)
        current-content (.. div -textContent)]
    (set! (.. div -textContent) (f current-content))))

(def button-types ["rock" "paper" "scissors"])

(defn increment!
  "Since textcontent exists as string, some hoops must be jumped through..."
  [n]
  ((comp str inc int) n))
;; Add event listeners to the rock/paper/scissors buttons
(doseq [button-type button-types]
  (.addEventListener (.getElementById js/document button-type) "click"
                     #(let [{:keys [ui-message winner]} (play-round (keyword button-type))]
                        (set-text-content! "ui-message" ui-message)
                        (case winner
                          :computer (update-text-content! "computer-score" increment!)
                          :user (update-text-content! "user-score" increment!)
                          nil))))

(defn init []
  (log "PLAY!!!"))
