(ns app.core
  (:require
   [clojure.string :as string]))

(def log (.-log js/console))

(def choices
  {0 "rock"
   1 "paper"
   2 "scissors"})

(def end-of-round-output
  {"rock" "Rock beats scissors."
   "paper" "Paper beats rock."
   "scissors" "Scissors beats paper."
   "tie" "Tie game."
   "error" "Invalid user input."})

(defn determine-winner [player-selection winning-selection]
  (if (= player-selection winning-selection)
    (do
      (log (str (get end-of-round-output winning-selection) " Player wins!"))
      {:winner :player})
    (do
      (log (str (get end-of-round-output winning-selection) " Computer wins!"))
      {:winner :computer})))

(defn get-computer-choice []
  (let [n (rand-int 3)]
    (get choices n)))

(defn play [player-selection-raw computer-selection]
  (let [player-selection (string/lower-case player-selection-raw)]
    (cond
      (not (seq (#{"rock" "paper" "scissors"} player-selection)))
      (do (log (get end-of-round-output "error"))
          {:winner :error})
      (= computer-selection player-selection)
      (do (log (get end-of-round-output "tie"))
          {:winner :tie})
      (= #{player-selection computer-selection} #{"rock" "scissors"})
      (determine-winner player-selection "rock")
      (= #{player-selection computer-selection} #{"rock" "paper"})
      (determine-winner player-selection "paper")
      (= #{player-selection computer-selection} #{"paper" "scissors"})
      (determine-winner player-selection "scissors"))))

(defn game []
  (loop [game-count 0
         computer-score 0
         player-score 0]
    (if (= game-count 5)
      (log (str "Player: " player-score ", Computer: " computer-score))
      (case (:winner (play (js/prompt "Rock Paper Scissors") (get-computer-choice)))
        :tie
        (recur game-count computer-score player-score)
        :error
        (recur game-count computer-score player-score)
        :player
        (recur (inc game-count) computer-score (inc player-score))
        :computer
        (recur (inc game-count) (inc computer-score) player-score)))))

(defn ^:export init []
  (game))
