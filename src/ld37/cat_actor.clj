(ns ld37.cat-actor
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Group)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.actions Actions
                                                    MoveToAction))
  (:require [ld37.asset-manager :as am]
            [ld37.common :as c]))

(defn make-cat-head-actor!
  [game]
  (let [last-head-position (atom nil)
        {:keys []} @game
        head (proxy [Image] [(.get am/manager "images/head.png" Texture)]
               (act [delta]
                 (let [{[[head-x head-y :as head-position] & _] :snake} @game
                       [screen-x screen-y] [(+ (- (* c/tile-width head-x)
                                                  (/ (.getWidth this) 2))
                                               (/ c/tile-width 2))
                                            (+ (- (* c/tile-width head-y)
                                                  (/ (.getHeight this) 2))
                                               (/ c/tile-width 2))]]
                   (cond (nil? @last-head-position)
                         (do (.setPosition this screen-x screen-y)
                             (reset! last-head-position head-position))

                         (not= @last-head-position head-position)
                         (do (.addAction this (Actions/moveTo screen-x screen-y c/game-speed))
                             (reset! last-head-position head-position))

                         ;; do nothing, already animating
                         :else nil))
                 (proxy-super act delta)))]
    head))

(defn make-cat-actor!
  [game]
  (let [{:keys [snake]} @game
        cat-actor (proxy [Group] []
                    )
        head (make-cat-head-actor! game)]
    (.addActor cat-actor head)
    cat-actor))
