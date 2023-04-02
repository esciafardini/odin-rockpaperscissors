(ns rps.core
  (:require
   [rps.js-helpers :refer [console-log get-element-by-id set-text-content!]]))

(def player-score (atom 0))
(def computer-score (atom 0))

(def computer-choices
  {0 :rock
   1 :paper
   2 :scissors})

(def ui-messages
  {:rock "Rock beats scissors"
   :paper "Paper beats rock"
   :scissors "Scissors beats paper"
   :tie "Tie game"})

(defn return-game-info [winner rps-selection-map]
  (merge rps-selection-map {:winner (get rps-selection-map winner)
                            :ui-message (get ui-messages winner)}))

(defn determine-winner
  "If there is a winner, a map will be returned.
   Otherwise, a keyword that signifies tie game (:tie)"
  [rps-selection-map]
  (let [choices (set (vals (select-keys rps-selection-map [:player-selection :computer-selection])))]
    (cond->> rps-selection-map
      (= (count choices) 1)
      (return-game-info :tie)

      (= choices #{:rock :paper})
      (return-game-info :paper)

      (= choices #{:rock :scissors})
      (return-game-info :rock)

      (= choices #{:scissors :paper})
      (return-game-info :scissors))))

(defn play-round [player-selection computer-selection]
  (let [rps-selection-map {:player-selection player-selection
                           :computer-selection computer-selection}]

    (if (= computer-selection player-selection)
      (determine-winner rps-selection-map)
      (determine-winner (merge rps-selection-map {player-selection :player ;inverting for lookup (if winner = scissors, who won?)
                                                  computer-selection :computer})))))

(defn get-computer-choice! []
  (let [n (rand-int 3)]
    (get computer-choices n)))

(defn set-text-and-images! [computer-selection player-selection score div-id]
  (set! (.. (get-element-by-id "computer-img") -src) (str "public/" (name computer-selection) ".png"))
  (set! (.. (get-element-by-id "player-img") -src) (str "public/" (name player-selection) ".png"))
  (and div-id (set-text-content! div-id score)))

(defn game-over [winner-div]
  (doseq [button-element (.querySelectorAll js/document "button")]
    (set! (.. button-element -disabled) true)
    (set! (.. button-element -style -visibility) "hidden"))
  (set-text-content! "ui-message" (if (= winner-div "player-score") "YOU WIN" "YOU LOSE")))

(defn victory-fx [computer-selection player-selection score div-id]
  (and score (swap! score inc))
  (if (= @score 5)
    (do
      (set-text-and-images! computer-selection player-selection @score div-id)
      (game-over div-id))
    (set-text-and-images! computer-selection player-selection @score div-id)))

(doseq [button-type ["rock" "paper" "scissors"]]
  (.addEventListener (get-element-by-id button-type) "click"
                     #(let [{:keys [player-selection computer-selection ui-message winner]} (play-round (keyword button-type) (get-computer-choice!))]
                        (set-text-content! "ui-message" ui-message)
                        (case winner
                          :computer (victory-fx computer-selection player-selection computer-score "computer-score")
                          :player (victory-fx computer-selection player-selection player-score "player-score")
                          (victory-fx computer-selection player-selection (atom nil) nil)))))

(defn init [])
