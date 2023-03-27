(ns rps.core
  (:require
   [clojure.set :as set]
   [rps.js-helpers :refer [console-log get-element-by-id set-text-content!]]
   [cljs.core :as c]))

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


(defn play-round [user-selection computer-selection]
  (let [game-info {:user user-selection
                   :computer computer-selection}]
    (determine-winner game-info)))

(def button-elements
  (.querySelectorAll js/document "button"))

(def user-score (atom 0))
(def computer-score (atom 0))

(defn get-computer-choice! []
  (let [n (rand-int 3)]
    (get computer-choices n)))

(defn game-over []
  (doseq [button-element button-elements]
    (set! (.. button-element -disabled) true))
  (set-text-content! "game-over-message" "Game over!!!!"))


(defn victory-fx [score div]
  (swap! score inc)
  (if (= @score 5)
    (do
      (set-text-content! div (str @score " (Winner)"))
      (game-over))
    (set-text-content! div @score)))

(def button-types ["rock" "paper" "scissors"])

(doseq [button-type button-types]
  (.addEventListener (get-element-by-id button-type) "click"
                     #(let [{:keys [ui-message winner]} (play-round (keyword button-type) (get-computer-choice!))]
                        (set-text-content! "ui-message" ui-message)
                        (case winner
                          :computer (victory-fx computer-score "computer-score")
                          :user (victory-fx user-score "user-score")
                          nil))))

(defn init []
  (console-log "PLAY!!!"))
