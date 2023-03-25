(ns app.core
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
  (let [choices (set (vals data))
        valid-entry? (set/subset? choices #{:rock :paper :scissors})]
    (cond
      (not valid-entry?)
      {:winner :error :ui-message (get-ui-message :error)}

      (= 1 (count choices))
      {:winner :tie :ui-message (get-ui-message :tie)}

      (= choices #{:rock :paper})
      (outcome :paper data)

      (= choices #{:rock :scissors})
      (outcome :rock data)

      (= choices #{:scissors :paper})
      (outcome :scissors data))))

(defn get-computer-choice []
  (let [n (rand-int 3)]
    (get computer-choices n)))

(defn play [user-input]
  (let [user-selection (keyword (string/lower-case user-input))
        computer-selection (get-computer-choice)
        game-info {:user user-selection
                   :computer computer-selection}]
    (determine-winner game-info)))

(defn game []
  (loop [game-count 0
         computer-score 0
         user-score 0]
    (if (= game-count 5)
      (log (str "User: " user-score ", Computer: " computer-score))
      (case (:winner (play (js/prompt "Rock Paper Scissors")))
        :tie
        (recur game-count computer-score user-score)
        :error
        (recur game-count computer-score user-score)
        :user
        (recur (inc game-count) computer-score (inc user-score))
        :computer
        (recur (inc game-count) (inc computer-score) user-score)))))

(defn ^:export init []
  (game))
