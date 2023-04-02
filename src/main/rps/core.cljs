(ns rps.core
  (:require
   [cljs.core :as c]
   [clojure.set :as set]
   [rps.js-helpers :refer [console-log get-element-by-id set-text-content!]]))

(def computer-choices
  {0 :rock
   1 :paper
   2 :scissors})

(def ui-messages
  {:rock "Rock beats scissors"
   :paper "Paper beats rock"
   :scissors "Scissors beats paper"
   :tie "Tie game"})

(defn get-winner
  "Returns keyword of :player or :computer based on winning-selection"
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
   Otherwise, a keyword that signifies tie game (:tie)"
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

(defn play-round [player-selection computer-selection]
  (let [game-info {:player player-selection
                   :computer computer-selection}]
    (merge game-info (determine-winner game-info))))

(def button-elements
  (.querySelectorAll js/document "button"))

(def player-score (atom 0))
(def computer-score (atom 0))

(defn get-computer-choice! []
  (let [n (rand-int 3)]
    (get computer-choices n)))

(defn game-over [winner-div]
  (doseq [button-element button-elements]
    (set! (.. button-element -disabled) true)
    (set! (.. button-element -style -visibility) "hidden"))

  (let [game-over-message (if (= winner-div "player-score")
                            "YOU WIN"
                            "YOU LOSE")]
    (set! (.. (get-element-by-id "ui-message") -style -fontWeight) "bold")
    (set-text-content! "ui-message" game-over-message)))

(defn victory-fx [score div-id computer player]
  (and score (swap! score inc))
  (if (and score (= @score 5))
    (do
      (set! (.. (get-element-by-id "computer-img") -src) (str (name computer) ".png"))
      (set! (.. (get-element-by-id "player-img") -src) (str (name player) ".png"))
      (set-text-content! div-id @score)
      (game-over div-id))
    (do
      (set! (.. (get-element-by-id "computer-img") -src) (str (name computer) ".png"))
      (set! (.. (get-element-by-id "player-img") -src) (str (name player) ".png"))
      (and div-id (set-text-content! div-id @score)))))

(def button-types ["rock" "paper" "scissors"])

(doseq [button-type button-types]
  (.addEventListener (get-element-by-id button-type) "click"
                     #(let [{:keys [player computer ui-message winner]} (play-round (keyword button-type) (get-computer-choice!))]
                        (set! (.. (get-element-by-id "computer-selection") -style -visibility) "visible")
                        (set! (.. (get-element-by-id "player-selection") -style -visibility) "visible")
                        (set-text-content! "ui-message" ui-message)
                        (case winner
                          :computer (victory-fx computer-score "computer-score" computer player)
                          :player (victory-fx player-score "player-score" computer player)
                          :tie (victory-fx nil nil computer player)))))

(defn init []
  (console-log "PLAY!!!"))
