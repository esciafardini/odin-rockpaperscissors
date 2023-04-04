(ns rps.core
  (:require
   [rps.js-helpers :refer [console-log get-element-by-id set-img-src! set-text-content!]]))

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

(defn return-game-info
  "At this fn call, rps-selection-map contains a lookup for winner"
  [winning-selection rps-selection-map]
  (merge rps-selection-map {:winner (get rps-selection-map winning-selection)
                            :ui-message (get ui-messages winning-selection)}))

(defn determine-winner
  [rps-selection-map]
  (let [choices (set (vals (select-keys rps-selection-map [:player-selection :computer-selection])))]
    (cond->> rps-selection-map
      (= (count choices) 1) (return-game-info :tie)
      (= choices #{:rock :paper}) (return-game-info :paper)
      (= choices #{:rock :scissors}) (return-game-info :rock)
      (= choices #{:scissors :paper}) (return-game-info :scissors))))

(defn play-round
  "If not a tie game, enhance rps-selection-map with a lookup for winner based on selection"
  [player-selection computer-selection]
  (let [rps-selection-map {:player-selection player-selection
                           :computer-selection computer-selection}]
    (if (= computer-selection player-selection)
      (determine-winner rps-selection-map)
      (determine-winner (merge rps-selection-map {player-selection :player
                                                  computer-selection :computer})))))

(defn get-computer-choice! []
  (let [n (rand-int 3)]
    (get computer-choices n)))

(defn set-text-and-images! [computer-selection player-selection score div-id]
  (set-img-src! "computer-img" (str "public/" (name computer-selection) ".png"))
  (set-img-src! "player-img" (str "public/" (name player-selection) ".png"))
  (and div-id (set-text-content! div-id score)))

(defn game-over! [winner-div]
  (doseq [button-element (.querySelectorAll js/document "button")]
    (set! (.. button-element -disabled) true)
    (set! (.. button-element -style -visibility) "hidden"))
  (set-text-content! "ui-message" (if (= winner-div "player-score") "YOU WIN" "YOU LOSE")))

(defn victory-fx! [computer-selection player-selection score div-id]
  (and score (swap! score inc))
  (if (= @score 5)
    (do
      (set-text-and-images! computer-selection player-selection @score div-id)
      (game-over! div-id))
    (set-text-and-images! computer-selection player-selection @score div-id)))

(defn init []
  (doseq [button-type ["rock" "paper" "scissors"]]
    (.addEventListener (get-element-by-id button-type) "click"
                       #(let [{:keys [player-selection computer-selection ui-message winner]} (play-round (keyword button-type) (get-computer-choice!))]
                          (set-text-content! "ui-message" ui-message)
                          (case winner
                            :computer (victory-fx! computer-selection player-selection computer-score "computer-score")
                            :player (victory-fx! computer-selection player-selection player-score "player-score")
                            (victory-fx! computer-selection player-selection (atom nil) nil))))))
