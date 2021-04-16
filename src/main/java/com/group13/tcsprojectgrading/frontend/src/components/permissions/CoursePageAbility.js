import {Ability, AbilityBuilder} from "@casl/ability";
import { createCanBoundTo } from "@casl/react";
import {isTA, isTeacher} from "./functions";

const ability = new Ability([])

function updateAbilityCoursePage(ability, role) {
  const { can, rules } = new AbilityBuilder();

  if (isTeacher(role)) {
    can(['read', 'write'], 'Projects')
    can(['sync'], 'Course')
    can(['read', 'write'], 'Statistic')
  } else if (isTA(role)) {
    can(['read'], 'Projects')
    can(['read'], 'Statistic')
  }

  ability.update(rules)
}

const Can = createCanBoundTo(ability)

export {Can, ability, updateAbilityCoursePage};
